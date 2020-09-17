(ns status-im.chat.models.input
  (:require [clojure.string :as string]
            [goog.object :as object]
            [status-im.chat.constants :as chat.constants]
            [status-im.chat.models :as chat]
            [status-im.chat.models.message :as chat.message]
            [status-im.chat.models.message-content :as message-content]
            [status-im.constants :as constants]
            [status-im.i18n :as i18n]
            [status-im.utils.datetime :as datetime]
            [status-im.utils.fx :as fx]
            ["emojilib" :as emojis]
            [status-im.chat.models.mentions :as mentions]
            [status-im.utils.utils :as utils]))

(defn text->emoji
  "Replaces emojis in a specified `text`"
  [text]
  (utils/safe-replace text
                      #":([a-z_\-+0-9]*):"
                      (fn [[original emoji-id]]
                        (if-let [emoji-map (object/get (.-lib emojis) emoji-id)]
                          (.-char ^js emoji-map)
                          original))))

(fx/defn set-chat-input-text
  "Set input text for current-chat. Takes db and input text and cofx
  as arguments and returns new fx. Always clear all validation messages."
  [{{:keys [current-chat-id] :as db} :db} new-input]
  {:db (assoc-in db [:chat/inputs current-chat-id :input-text] (text->emoji new-input))})

(fx/defn select-mention
  [{:keys [db] :as cofx} {:keys [alias name searched-text match] :as user}]
  (let [chat-id     (:current-chat-id db)
        new-text    (mentions/new-input-text-with-mention cofx user)
        at-sign-idx (get-in db [:chats chat-id :mentions :at-sign-idx])]
    (fx/merge
     cofx
     {:db (-> db
              (assoc-in [:chats/cursor chat-id] (+ at-sign-idx (count name) 2))
              (assoc-in [:chats/mention-suggestions chat-id] nil))}
     (set-chat-input-text new-text)
     ;; NOTE(roman): on-text-input event is not dispatched when we change input
     ;; programmatically, so we have to call `on-text-input` manually 
     (mentions/on-text-input
      (let [match-len (count match)
            searched-text-len (count searched-text)]
        {:new-text      (subs match searched-text-len)
         :previous-text ""
         :start         (+ at-sign-idx searched-text-len 1)
         :end           (+ at-sign-idx match-len 1)}))
     (mentions/recheck-at-idxs {alias user}))))

(defn- start-cooldown [{:keys [db]} cooldowns]
  {:dispatch-later        [{:dispatch [:chat/disable-cooldown]
                            :ms       (chat.constants/cooldown-periods-ms
                                       cooldowns
                                       chat.constants/default-cooldown-period-ms)}]
   :show-cooldown-warning nil
   :db                    (assoc db
                                 :chat/cooldowns (if (= chat.constants/cooldown-reset-threshold cooldowns)
                                                   0
                                                   cooldowns)
                                 :chat/spam-messages-frequency 0
                                 :chat/cooldown-enabled? true)})

(fx/defn process-cooldown
  "Process cooldown to protect against message spammers"
  [{{:keys [chat/last-outgoing-message-sent-at
            chat/cooldowns
            chat/spam-messages-frequency
            current-chat-id] :as db} :db :as cofx}]
  (when (chat/public-chat? cofx current-chat-id)
    (let [spamming-fast? (< (- (datetime/timestamp) last-outgoing-message-sent-at)
                            (+ chat.constants/spam-interval-ms (* 1000 cooldowns)))
          spamming-frequently? (= chat.constants/spam-message-frequency-threshold spam-messages-frequency)]
      (cond-> {:db (assoc db
                          :chat/last-outgoing-message-sent-at (datetime/timestamp)
                          :chat/spam-messages-frequency (if spamming-fast?
                                                          (inc spam-messages-frequency)
                                                          0))}

        (and spamming-fast? spamming-frequently?)
        (start-cooldown (inc cooldowns))))))

(fx/defn reply-to-message
  "Sets reference to previous chat message and focuses on input"
  [{:keys [db] :as cofx} message]
  (let [current-chat-id (:current-chat-id db)]
    (fx/merge cofx
              {:db (-> db
                       (assoc-in [:chats current-chat-id :metadata :responding-to-message]
                                 message)
                       (update-in [:chats current-chat-id :metadata]
                                  dissoc :sending-image))})))

(fx/defn cancel-message-reply
  "Cancels stage message reply"
  [{:keys [db]}]
  (let [current-chat-id (:current-chat-id db)]
    {:db (assoc-in db [:chats current-chat-id :metadata :responding-to-message] nil)}))

(fx/defn send-plain-text-message
  "when not empty, proceed by sending text message"
  [{:keys [db] :as cofx} input-text current-chat-id]
  (when-not (string/blank? input-text)
    (let [{:keys [message-id]}
          (get-in db [:chats current-chat-id :metadata :responding-to-message])
          preferred-name (get-in db [:multiaccount :preferred-name])
          emoji? (message-content/emoji-only-content? {:text input-text
                                                       :response-to message-id})]
      (fx/merge cofx
                {:db (assoc-in db [:chats current-chat-id :metadata :responding-to-message] nil)}
                (chat.message/send-message {:chat-id      current-chat-id
                                            :content-type (if emoji?
                                                            constants/content-type-emoji
                                                            constants/content-type-text)
                                            :text input-text
                                            :response-to message-id
                                            :ens-name preferred-name})
                (set-chat-input-text nil)
                (process-cooldown)))))

(fx/defn send-image
  [{{:keys [current-chat-id] :as db} :db :as cofx}]
  (let [image-path (get-in db [:chats current-chat-id :metadata :sending-image :uri])]
    (fx/merge cofx
              {:db (update-in db [:chats current-chat-id :metadata] dissoc :sending-image)}
              (when-not (string/blank? image-path)
                (chat.message/send-message {:chat-id      current-chat-id
                                            :content-type constants/content-type-image
                                            :image-path   (utils/safe-replace image-path #"file://" "")
                                            :text         (i18n/label :t/update-to-see-image)})))))

(fx/defn send-audio-message
  [cofx audio-path duration current-chat-id]
  (when-not (string/blank? audio-path)
    (chat.message/send-message cofx {:chat-id           current-chat-id
                                     :content-type      constants/content-type-audio
                                     :audio-path        audio-path
                                     :audio-duration-ms duration
                                     :text              (i18n/label :t/update-to-listen-audio)})))

(fx/defn send-sticker-message
  [cofx {:keys [hash pack]} current-chat-id]
  (when-not (string/blank? hash)
    (chat.message/send-message cofx {:chat-id      current-chat-id
                                     :content-type constants/content-type-sticker
                                     :sticker {:hash hash
                                               :pack pack}
                                     :text    (i18n/label :t/update-to-see-sticker)})))

(fx/defn send-current-message
  "Sends message from current chat input"
  [{{:keys [current-chat-id] :as db} :db :as cofx}]
  (let [{:keys [input-text]} (get-in db [:chat/inputs current-chat-id])
        input-text-with-mentions (mentions/check-mentions cofx input-text)]
    (fx/merge cofx
              (send-image)
              (send-plain-text-message input-text-with-mentions current-chat-id)
              (mentions/clear-suggestions)
              (mentions/clear-cursor))))
