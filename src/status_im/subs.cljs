(ns status-im.subs
  (:require [cljs.spec.alpha :as spec]
            [clojure.string :as string]
            [re-frame.core :as re-frame]
            [status-im.browser.core :as browser]
            [status-im.chat.db :as chat.db]
            [status-im.chat.models :as chat.models]
            [status-im.chat.models.message-list :as models.message-list]
            [status-im.constants :as constants]
            [status-im.contact.db :as contact.db]
            [status-im.ens.core :as ens]
            [status-im.ethereum.core :as ethereum]
            [status-im.ethereum.stateofus :as stateofus]
            [status-im.ethereum.tokens :as tokens]
            [status-im.ethereum.transactions.core :as transactions]
            [status-im.fleet.core :as fleet]
            [status-im.group-chats.db :as group-chats.db]
            [status-im.i18n :as i18n]
            [status-im.multiaccounts.core :as multiaccounts]
            [status-im.multiaccounts.db :as multiaccounts.db]
            [status-im.multiaccounts.model :as multiaccounts.model]
            [status-im.multiaccounts.recover.core :as recover]
            [status-im.chat.models.reactions :as models.reactions]
            [status-im.pairing.core :as pairing]
            [status-im.signing.gas :as signing.gas]
            #_[status-im.tribute-to-talk.core :as tribute-to-talk]
            [status-im.tribute-to-talk.db :as tribute-to-talk.db]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.screens.add-new.new-public-chat.db :as db]
            [status-im.ui.screens.mobile-network-settings.utils
             :as
             mobile-network-utils]
            [status-im.utils.build :as build]
            [status-im.utils.config :as config]
            [status-im.utils.datetime :as datetime]
            [status-im.utils.gfycat.core :as gfycat]
            [status-im.utils.money :as money]
            [status-im.utils.security :as security]
            [status-im.wallet.db :as wallet.db]
            [status-im.wallet.utils :as wallet.utils]
            [status-im.utils.utils :as utils]
            status-im.ui.screens.keycard.subs
            status-im.ui.screens.keycard.settings.subs
            status-im.ui.screens.keycard.pin.subs
            status-im.ui.screens.keycard.setup.subs))

;; TOP LEVEL ===========================================================================================================

(defn reg-root-key-sub [sub-name db-key]
  (re-frame/reg-sub sub-name (fn [db] (get db db-key))))

;;view
(reg-root-key-sub :view-id :view-id)
(reg-root-key-sub :screen-params :navigation/screen-params)

;;bottom sheet
(reg-root-key-sub :bottom-sheet/show? :bottom-sheet/show?)
(reg-root-key-sub :bottom-sheet/view :bottom-sheet/view)
(reg-root-key-sub :bottom-sheet/options :bottom-sheet/options)

;;general
(reg-root-key-sub :sync-state :sync-state)
(reg-root-key-sub :network-status :network-status)
(reg-root-key-sub :network/type :network/type)
(reg-root-key-sub :peers-count :peers-count)
(reg-root-key-sub :about-app/node-info :node-info)
(reg-root-key-sub :peers-summary :peers-summary)
(reg-root-key-sub :dimensions/window :dimensions/window)
(reg-root-key-sub :initial-props :initial-props)
(reg-root-key-sub :fleets/custom-fleets :custom-fleets)
(reg-root-key-sub :animations :animations)
(reg-root-key-sub :ui/search :ui/search)
(reg-root-key-sub :web3-node-version :web3-node-version)
(reg-root-key-sub :keyboard-height :keyboard-height)
(reg-root-key-sub :keyboard-max-height :keyboard-max-height)
(reg-root-key-sub :sync-data :sync-data)
(reg-root-key-sub :mobile-network/remember-choice? :mobile-network/remember-choice?)
(reg-root-key-sub :qr-modal :qr-modal)
(reg-root-key-sub :bootnodes/manage :bootnodes/manage)
(reg-root-key-sub :networks/current-network :networks/current-network)
(reg-root-key-sub :networks/networks :networks/networks)
(reg-root-key-sub :networks/manage :networks/manage)
(reg-root-key-sub :get-pairing-installations :pairing/installations)
(reg-root-key-sub :tooltips :tooltips)
(reg-root-key-sub :supported-biometric-auth :supported-biometric-auth)
(reg-root-key-sub :app-active-since :app-active-since)
(reg-root-key-sub :connectivity/ui-status-properties :connectivity/ui-status-properties)
(reg-root-key-sub :logged-in-since :logged-in-since)

;;NOTE this one is not related to ethereum network
;; it is about cellular network/ wifi network
(reg-root-key-sub :network/type :network/type)

;;profile
(reg-root-key-sub :my-profile/seed :my-profile/seed)
(reg-root-key-sub :my-profile/advanced? :my-profile/advanced?)
(reg-root-key-sub :my-profile/editing? :my-profile/editing?)
(reg-root-key-sub :my-profile/profile :my-profile/profile)
(reg-root-key-sub :profile/photo-added? :profile/photo-added?)

;;multiaccount
(reg-root-key-sub :multiaccounts/multiaccounts :multiaccounts/multiaccounts)
(reg-root-key-sub :multiaccounts/login :multiaccounts/login)
(reg-root-key-sub :multiaccount :multiaccount)
(reg-root-key-sub :multiaccount/accounts :multiaccount/accounts)
(reg-root-key-sub :get-recover-multiaccount :multiaccounts/recover)
;;chat
(reg-root-key-sub ::cooldown-enabled? :chat/cooldown-enabled?)
(reg-root-key-sub ::chats :chats)
(reg-root-key-sub ::chat-ui-props :chat-ui-props)
(reg-root-key-sub :chats/current-chat-id :current-chat-id)
(reg-root-key-sub :public-group-topic :public-group-topic)
(reg-root-key-sub :chats/loading? :chats/loading?)
(reg-root-key-sub :new-chat-name :new-chat-name)
(reg-root-key-sub :group-chat-profile/editing? :group-chat-profile/editing?)
(reg-root-key-sub :group-chat-profile/profile :group-chat-profile/profile)
(reg-root-key-sub :selected-participants :selected-participants)
(reg-root-key-sub :chat/inputs :chat/inputs)
(reg-root-key-sub :chat/memberships :chat/memberships)
(reg-root-key-sub :camera-roll-photos :camera-roll-photos)
(reg-root-key-sub :group-chat/invitations :group-chat/invitations)
(reg-root-key-sub :chats/mention-suggestions :chats/mention-suggestions)
(reg-root-key-sub :chats/cursor :chats/cursor)
(reg-root-key-sub :chats/input-with-mentions :chats/input-with-mentions)
;;browser
(reg-root-key-sub :browsers :browser/browsers)
(reg-root-key-sub :browser/options :browser/options)
(reg-root-key-sub :dapps/permissions :dapps/permissions)

;;stickers
(reg-root-key-sub :stickers/selected-pack :stickers/selected-pack)
(reg-root-key-sub :stickers/packs :stickers/packs)
(reg-root-key-sub :stickers/installed-packs :stickers/packs-installed)
(reg-root-key-sub :stickers/packs-owned :stickers/packs-owned)
(reg-root-key-sub :stickers/packs-pending :stickers/packs-pending)

;;mailserver
(reg-root-key-sub :mailserver/current-id :mailserver/current-id)
(reg-root-key-sub :mailserver/mailservers :mailserver/mailservers)
(reg-root-key-sub :mailserver.edit/mailserver :mailserver.edit/mailserver)
(reg-root-key-sub :mailserver/state :mailserver/state)
(reg-root-key-sub :mailserver/pending-requests :mailserver/pending-requests)
(reg-root-key-sub :mailserver/request-error? :mailserver/request-error)
(reg-root-key-sub :mailserver/fetching-gaps-in-progress :mailserver/fetching-gaps-in-progress)
(reg-root-key-sub :mailserver/gaps :mailserver/gaps)
(reg-root-key-sub :mailserver/ranges :mailserver/ranges)

;;contacts
(reg-root-key-sub ::contacts :contacts/contacts)
(reg-root-key-sub :contacts/current-contact-identity :contacts/identity)
(reg-root-key-sub :contacts/new-identity :contacts/new-identity)
(reg-root-key-sub :group/selected-contacts :group/selected-contacts)
(reg-root-key-sub :contacts/blocked-set :contacts/blocked)

;;wallet
(reg-root-key-sub :wallet :wallet)
(reg-root-key-sub :prices :prices)
(reg-root-key-sub :collectibles :collectibles)
(reg-root-key-sub :wallet/all-tokens :wallet/all-tokens)
(reg-root-key-sub :prices-loading? :prices-loading?)
(reg-root-key-sub :wallet.transactions :wallet.transactions)
(reg-root-key-sub :wallet/custom-token-screen :wallet/custom-token-screen)
(reg-root-key-sub :wallet/prepare-transaction :wallet/prepare-transaction)
(reg-root-key-sub :wallet-service/manual-setting :wallet-service/manual-setting)
(reg-root-key-sub :wallet-service/state :wallet-service/state)

;;commands
(reg-root-key-sub :commands/select-account :commands/select-account)

;;ethereum
(reg-root-key-sub :ethereum/current-block :ethereum/current-block)

;;ens
(reg-root-key-sub :ens/registration :ens/registration)
(reg-root-key-sub :ens/registrations :ens/registrations)
(reg-root-key-sub :ens/names :ens/names)

;;signing
(reg-root-key-sub :signing/sign :signing/sign)
(reg-root-key-sub :signing/tx :signing/tx)
(reg-root-key-sub :signing/edit-fee :signing/edit-fee)

;;intro-wizard
(reg-root-key-sub :intro-wizard-state :intro-wizard)

(reg-root-key-sub :popover/popover :popover/popover)
(reg-root-key-sub :add-account :add-account)

(reg-root-key-sub :keycard :keycard)

(reg-root-key-sub :auth-method :auth-method)

(reg-root-key-sub :multiaccounts/loading :multiaccounts/loading)

(reg-root-key-sub ::messages :messages)
(reg-root-key-sub ::reactions :reactions)
(reg-root-key-sub ::message-lists :message-lists)
(reg-root-key-sub ::pagination-info :pagination-info)

;; keycard
(reg-root-key-sub :keycard/new-account-sheet? :keycard/new-account-sheet?)

;; delete profile
(reg-root-key-sub :delete-profile/error :delete-profile/error)

;; push notifications
(reg-root-key-sub :push-notifications/servers :push-notifications/servers)

;;GENERAL ==============================================================================================================

(re-frame/reg-sub
 :multiaccount/logged-in?
 (fn [db]
   (multiaccounts.model/logged-in? {:db db})))

;; Intro wizard
(re-frame/reg-sub
 :intro-wizard
 :<- [:intro-wizard-state]
 :<- [:dimensions/window]
 (fn [[wizard-state {:keys [width height]}]]
   (assoc wizard-state
          :view-height height :view-width width)))

(re-frame/reg-sub
 :intro-wizard/generate-key
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state [:processing? :view-height])))

(re-frame/reg-sub
 :intro-wizard/choose-key
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state [:multiaccounts :selected-id :view-height])))

(re-frame/reg-sub
 :intro-wizard/select-key-storage
 :<- [:intro-wizard]
 (fn [wizard-state]
   (merge (select-keys wizard-state [:selected-storage-type :view-height :recovering?])
          (if (:recovering? wizard-state)
            {:forward-action :multiaccounts.recover/select-storage-next-pressed}
            {:forward-action :intro-wizard/step-forward-pressed}))))

(re-frame/reg-sub
 :intro-wizard/create-code
 :<- [:intro-wizard]
 (fn [wizard-state]
   (merge (select-keys wizard-state [:processing?])
          (if (:recovering? wizard-state)
            {:forward-action  :multiaccounts.recover/enter-password-next-pressed}
            {:forward-action :intro-wizard/step-forward-pressed}))))

(re-frame/reg-sub
 :intro-wizard/enter-phrase
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state [:processing?
                              :passphrase-word-count
                              :next-button-disabled?
                              :passphrase-error])))

(re-frame/reg-sub
 :intro-wizard/recovery-success
 :<- [:intro-wizard]
 (fn [wizard-state]
   {:pubkey (get-in wizard-state [:derived constants/path-whisper-keyword :public-key])
    :name (get-in wizard-state [:derived constants/path-whisper-keyword :name])
    :photo-path (get-in wizard-state [:derived constants/path-whisper-keyword :photo-path])
    :processing? (:processing? wizard-state)}))

(re-frame/reg-sub
 :intro-wizard/recover-existing-account?
 :<- [:intro-wizard]
 :<- [:multiaccounts/multiaccounts]
 (fn [[intro-wizard multiaccounts]]
   (recover/existing-account? (:root-key intro-wizard) multiaccounts)))

(re-frame/reg-sub
 :current-network
 :<- [:networks/networks]
 :<- [:networks/current-network]
 (fn [[networks current-network]]
   (when-let [network (get networks current-network)]
     (assoc network
            :rpc-network? (get-in network [:config :UpstreamConfig :Enabled])))))

(re-frame/reg-sub
 :chain-name
 :<- [:current-network]
 (fn [network]
   (ethereum/network->chain-name network)))

(re-frame/reg-sub
 :chain-id
 :<- [:current-network]
 (fn [network]
   (ethereum/network->chain-id network)))

(re-frame/reg-sub
 :mainnet?
 :<- [:chain-id]
 (fn [chain-id]
   (= 1 chain-id)))

(re-frame/reg-sub
 :network-name
 :<- [:current-network]
 (fn [network]
   (:name network)))

(re-frame/reg-sub
 :disconnected?
 :<- [:peers-count]
 (fn [peers-count]
   (and (not config/nimbus-enabled?) (zero? peers-count))))

(re-frame/reg-sub
 :offline?
 :<- [:network-status]
 :<- [:sync-state]
 :<- [:disconnected?]
 (fn [[network-status sync-state disconnected?]]
   (or disconnected?
       (= network-status :offline)
       (= sync-state :offline))))

(re-frame/reg-sub
 :syncing?
 :<- [:sync-state]
 (fn [sync-state]
   (#{:pending :in-progress} sync-state)))

(re-frame/reg-sub
 :dimensions/window-width
 :<- [:dimensions/window]
 :width)

(re-frame/reg-sub
 :dimensions/window-height
 :<- [:dimensions/window]
 :height)

(re-frame/reg-sub
 :dimensions/small-screen?
 :<- [:dimensions/window-height]
 (fn [height]
   (< height 550)))

(re-frame/reg-sub
 :get-screen-params
 :<- [:screen-params]
 :<- [:view-id]
 (fn [[params view-id-db] [_ view-id]]
   (get params (or view-id view-id-db))))

(re-frame/reg-sub
 :delete-swipe-position
 :<- [:animations]
 (fn [animations [_ type item-id]]
   (get-in animations [type item-id :delete-swiped])))

(re-frame/reg-sub
 :search/home-filter
 :<- [:ui/search]
 (fn [search]
   (get search :home-filter)))

(re-frame/reg-sub
 :search/currency-filter
 :<- [:ui/search]
 (fn [search]
   (get search :currency-filter)))

(re-frame/reg-sub
 :search/token-filter
 :<- [:ui/search]
 (fn [search]
   (get search :token-filter)))

(defn- node-version [web3-node-version]
  (or web3-node-version "N/A"))

(def app-short-version
  (let [version build/build-no]
    (str build/version " (" version ")")))

(re-frame/reg-sub
 :get-app-version
 :<- [:web3-node-version]
 (fn [web3-node-version]
   (str app-short-version "; " (node-version web3-node-version))))

(re-frame/reg-sub
 :get-app-short-version
 (fn [_] app-short-version))

(re-frame/reg-sub
 :get-app-node-version
 :<- [:web3-node-version]
 node-version)

(re-frame/reg-sub
 :my-profile/recovery
 :<- [:my-profile/seed]
 (fn [seed]
   (or seed {:step :intro})))

(re-frame/reg-sub
 :bottom-sheet
 :<- [:bottom-sheet/show?]
 :<- [:bottom-sheet/view]
 (fn [[show? view]]
   {:show? show?
    :view  view}))

(re-frame/reg-sub
 :is-contact-selected?
 :<- [:group/selected-contacts]
 (fn [selected-contacts [_ element]]
   (-> selected-contacts
       (contains? element))))

(re-frame/reg-sub
 :is-participant-selected?
 :<- [:selected-participants]
 (fn [selected-participants [_ element]]
   (-> selected-participants
       (contains? element))))

(re-frame/reg-sub
 :ethereum/chain-keyword
 :<- [:current-network]
 (fn [network]
   (ethereum/network->chain-keyword network)))

(re-frame/reg-sub
 :ethereum/native-currency
 :<- [:ethereum/chain-keyword]
 (fn [chain-keyword]
   (tokens/native-currency chain-keyword)))

;;MULTIACCOUNT ==============================================================================================================

(re-frame/reg-sub
 :multiaccount/public-key
 :<- [:multiaccount]
 (fn [{:keys [public-key]}]
   public-key))

(re-frame/reg-sub
 :multiaccount/preferred-name
 :<- [:multiaccount]
 (fn [{:keys [preferred-name]}]
   preferred-name))

(re-frame/reg-sub
 :multiaccount/default-account
 :<- [:multiaccount/accounts]
 (fn [accounts]
   (ethereum/get-default-account accounts)))

(re-frame/reg-sub
 :sign-in-enabled?
 :<- [:multiaccounts/login]
 (fn [{:keys [password]}]
   (spec/valid? ::multiaccounts.db/password
                (security/safe-unmask-data password))))

(re-frame/reg-sub
 :fleets/current-fleet
 :<- [:multiaccount]
 (fn [multiaccount]
   (fleet/current-fleet-sub multiaccount)))

(re-frame/reg-sub
 :log-level/current-log-level
 :<- [:multiaccount]
 (fn [multiaccount]
   (get multiaccount :log-level)))

(re-frame/reg-sub
 :waku/bloom-filter-mode
 :<- [:multiaccount]
 (fn [multiaccount]
   (boolean (get multiaccount :waku-bloom-filter-mode))))

(re-frame/reg-sub
 :dapps-address
 :<- [:multiaccount]
 (fn [acc]
   (get acc :dapps-address)))

(re-frame/reg-sub
 :dapps-account
 :<- [:multiaccount/accounts]
 :<- [:dapps-address]
 (fn [[accounts address]]
   (some #(when (= (:address %) address) %) accounts)))

(re-frame/reg-sub
 :multiaccount/current-account
 :<- [:multiaccount/accounts]
 :<- [:get-screen-params :wallet-account]
 (fn [[accounts acc]]
   (some #(when (= (:address %) (:address acc)) %) accounts)))

(re-frame/reg-sub
 :account-by-address
 :<- [:multiaccount/accounts]
 (fn [accounts [_ address]]
   (some #(when (= (:address %) address) %) accounts)))

(re-frame/reg-sub
 :multiple-multiaccounts?
 :<- [:multiaccounts/multiaccounts]
 (fn [multiaccounts]
   (> (count multiaccounts) 1)))

(re-frame/reg-sub
 :multiaccounts.login/keycard-account?
 :<- [:multiaccounts/multiaccounts]
 :<- [:multiaccounts/login]
 (fn [[multiaccounts {:keys [key-uid]}]]
   (get-in multiaccounts [key-uid :keycard-pairing])))

(re-frame/reg-sub
 :accounts-without-watch-only
 :<- [:multiaccount/accounts]
 (fn [accounts]
   (filter #(not= (:type %) :watch) accounts)))

(re-frame/reg-sub
 :add-account-disabled?
 :<- [:multiaccount/accounts]
 :<- [:add-account]
 (fn [[accounts {:keys [address type account seed private-key]}]]
   (or (string/blank? (:name account))
       (case type
         :generate
         false
         :watch
         (or (not (ethereum/address? address))
             (some #(when (= (:address %) address) %) accounts))
         :key
         (string/blank? (security/safe-unmask-data private-key))
         :seed
         (string/blank? (security/safe-unmask-data seed))
         false))))

;;CHAT ==============================================================================================================

(re-frame/reg-sub
 :get-collectible-token
 :<- [:collectibles]
 (fn [collectibles [_ {:keys [symbol token]}]]
   (get-in collectibles [(keyword symbol) (js/parseInt token)])))

(re-frame/reg-sub
 :chats/chat
 :<- [:chats/active-chats]
 (fn [chats [_ chat-id]]
   (get chats chat-id)))

(re-frame/reg-sub
 :chats/current-chat-ui-props
 :<- [::chat-ui-props]
 :<- [:chats/current-chat-id]
 (fn [[chat-ui-props id]]
   (get chat-ui-props id)))

(re-frame/reg-sub
 :chats/current-chat-ui-prop
 :<- [:chats/current-chat-ui-props]
 (fn [ui-props [_ prop]]
   (get ui-props prop)))

(re-frame/reg-sub
 :chats/active-chats
 :<- [::chats]
 (fn [chats]
   (reduce-kv (fn [acc id {:keys [is-active] :as chat}]
                (if is-active
                  (assoc acc id chat)
                  acc))
              {}
              chats)))

(re-frame/reg-sub
 ::chat
 :<- [::chats]
 (fn [chats [_ chat-id]]
   (get chats chat-id)))

(re-frame/reg-sub
 :chats/current-raw-chat
 :<- [::chats]
 :<- [:chats/current-chat-id]
 (fn [[chats current-chat-id]]
   (get chats current-chat-id)))

(re-frame/reg-sub
 :chats/current-chat-input-text
 :<- [:chats/current-chat-id]
 :<- [:chat/inputs]
 (fn [[chat-id inputs]]
   (get-in inputs [chat-id :input-text])))

(re-frame/reg-sub
 :chats/current-chat-membership
 :<- [:chats/current-chat-id]
 :<- [:chat/memberships]
 (fn [[chat-id memberships]]
   (get memberships chat-id)))

(re-frame/reg-sub
 :chats/current-chat
 :<- [:chats/current-raw-chat]
 :<- [:multiaccount/public-key]
 (fn [[{:keys [group-chat] :as current-chat}
       my-public-key]]
   (when current-chat
     (cond-> current-chat
       (chat.models/public-chat? current-chat)
       (assoc :show-input? true)

       (and (chat.models/group-chat? current-chat)
            (group-chats.db/joined? my-public-key current-chat))
       (assoc :show-input? true
              :joined? true)

       (not group-chat)
       (assoc :show-input? true)))))

(re-frame/reg-sub
 :current-chat/metadata
 :<- [:chats/current-raw-chat]
 (fn [current-chat]
   (select-keys current-chat
                [:public? :group-chat :chat-id :chat-name :color :invitation-admin])))

(re-frame/reg-sub
 :current-chat/one-to-one-chat?
 :<- [:chats/current-raw-chat]
 (fn [current-chat]
   (not (or (chat.models/group-chat? current-chat)
            (chat.models/public-chat? current-chat)))))

(re-frame/reg-sub
 :current-chat/public?
 :<- [:chats/current-raw-chat]
 (fn [current-chat]
   (chat.models/public-chat? current-chat)))

(re-frame/reg-sub
 :chats/current-chat-messages
 :<- [::messages]
 :<- [:chats/current-chat-id]
 (fn [[messages chat-id]]
   (get messages chat-id {})))

(re-frame/reg-sub
 :chats/message-reactions
 :<- [:multiaccount/public-key]
 :<- [::reactions]
 :<- [:chats/current-chat-id]
 (fn [[current-public-key reactions chat-id] [_ message-id]]
   (models.reactions/message-reactions
    current-public-key
    (get-in reactions [chat-id message-id]))))

(re-frame/reg-sub
 :chats/messages-gaps
 :<- [:mailserver/gaps]
 :<- [:chats/current-chat-id]
 (fn [[gaps chat-id]]
   (sort-by :from (vals (get gaps chat-id)))))

(re-frame/reg-sub
 :mailserver/ranges-by-chat-id
 :<- [:mailserver/ranges]
 (fn [ranges [_ chat-id]]
   (get ranges chat-id)))

(re-frame/reg-sub
 :chats/range
 :<- [:mailserver/ranges]
 :<- [:chats/current-chat-id]
 (fn [[ranges chat-id]]
   (get ranges chat-id)))

(re-frame/reg-sub
 :chats/all-loaded?
 :<- [::pagination-info]
 :<- [:chats/current-chat-id]
 (fn [[pagination-info chat-id]]
   (get-in pagination-info [chat-id :all-loaded?])))

(re-frame/reg-sub
 :chats/public?
 :<- [:chats/current-raw-chat]
 (fn [chat]
   (:public? chat)))

(re-frame/reg-sub
 :chats/message-list
 :<- [::message-lists]
 :<- [:chats/current-chat-id]
 (fn [[message-lists chat-id]]
   (get message-lists chat-id)))

(defn hydrate-messages
  "Pull data from messages and add it to the sorted list"
  [message-list messages]
  (keep #(if (= :message (% :type))
           (when-let [message (messages (% :message-id))]
             (merge message %))
           %)
        message-list))

(re-frame/reg-sub
 :chats/current-chat-no-messages?
 :<- [:chats/current-chat-messages]
 (fn [messages]
   (empty? messages)))

(re-frame/reg-sub
 :chats/current-chat-messages-stream
 :<- [:chats/message-list]
 :<- [:chats/current-chat-messages]
 :<- [:chats/messages-gaps]
 :<- [:chats/range]
 :<- [:chats/all-loaded?]
 :<- [:chats/public?]
 (fn [[message-list messages messages-gaps range all-loaded? public?]]
   ;;TODO (perf) we need to move all these to status-go
   (-> (models.message-list/->seq message-list)
       (chat.db/add-datemarks)
       (hydrate-messages messages)
       (chat.db/add-gaps messages-gaps range all-loaded? public?))))

(re-frame/reg-sub
 :chats/photo-path
 :<- [:contacts/contacts]
 :<- [:multiaccount]
 (fn [[contacts multiaccount] [_ id]]
   (multiaccounts/displayed-photo (or (get contacts id)
                                      (when (= id (:public-key multiaccount))
                                        multiaccount)
                                      (contact.db/public-key->new-contact id)))))

(re-frame/reg-sub
 :chats/unread-messages-number
 :<- [:chats/active-chats]
 (fn [chats _]
   (reduce-kv (fn [{:keys [public other]} _ {:keys [unviewed-messages-count public?]}]
                (if public?
                  {:public (+ public unviewed-messages-count)
                   :other other}
                  {:other (+ other unviewed-messages-count)
                   :public public}))
              {:public 0
               :other 0}
              chats)))

(re-frame/reg-sub
 :chats/cooldown-enabled?
 :<- [:chats/current-chat]
 :<- [::cooldown-enabled?]
 (fn [[{:keys [public?]} cooldown-enabled?]]
   (and public?
        cooldown-enabled?)))

(re-frame/reg-sub
 :chats/reply-message
 :<- [:chats/current-chat]
 (fn [{:keys [metadata]}]
   (:responding-to-message metadata)))

(re-frame/reg-sub
 :chats/sending-image
 :<- [:chats/current-chat]
 (fn [{:keys [metadata]}]
   (get-in metadata [:sending-image])))

(re-frame/reg-sub
 :public-chat.new/topic-error-message
 :<- [:public-group-topic]
 (fn [topic]
   (when-not (or (empty? topic)
                 (db/valid-topic? topic))
     (i18n/label :topic-name-error))))

(defn filter-selected-contacts
  [selected-contacts contacts]
  (filter #(contact.db/added? (contacts %)) selected-contacts))

(re-frame/reg-sub
 :selected-contacts-count
 :<- [:group/selected-contacts]
 :<- [:contacts/contacts]
 (fn [[selected-contacts contacts]]
   (count (filter-selected-contacts selected-contacts contacts))))

(re-frame/reg-sub
 :selected-participants-count
 :<- [:selected-participants]
 (fn [selected-participants]
   (count selected-participants)))

(defn filter-contacts [selected-contacts active-contacts]
  (filter #(selected-contacts (:public-key %)) active-contacts))

(re-frame/reg-sub
 :selected-group-contacts
 :<- [:group/selected-contacts]
 :<- [:contacts/active]
 (fn [[selected-contacts active-contacts]]
   (filter-contacts selected-contacts active-contacts)))

(re-frame/reg-sub
 :group-chat/inviter-info
 (fn [[_ chat-id] _]
   [(re-frame/subscribe [::chat chat-id])
    (re-frame/subscribe [:multiaccount/public-key])])
 (fn [[chat my-public-key]]
   {:joined? (group-chats.db/joined? my-public-key chat)
    :inviter-pk (group-chats.db/get-inviter-pk my-public-key chat)}))

(re-frame/reg-sub
 :group-chat/invitations-by-chat-id
 :<- [:group-chat/invitations]
 (fn [invitations [_ chat-id]]
   (filter #(= (:chat-id %) chat-id) (vals invitations))))

(re-frame/reg-sub
 :group-chat/pending-invitations-by-chat-id
 (fn [[_ chat-id] _]
   [(re-frame/subscribe [:group-chat/invitations-by-chat-id chat-id])])
 (fn [[invitations]]
   (filter #(= constants/invitation-state-requested (:state %)) invitations)))

(re-frame/reg-sub
 :chats/transaction-status
 ;;TODO address here for transactions
 :<- [:wallet/transactions]
 :<- [:ethereum/current-block]
 (fn [[transactions current-block] [_ hash]]
   (when-let [transaction (get transactions hash)]
     {:exists? true
      :confirmed?
      (-> transaction
          (wallet.db/get-confirmations current-block)
          (>= transactions/confirmations-count-threshold))})))

(re-frame/reg-sub
 :chats/mentionable-contacts
 :<- [:contacts/contacts]
 (fn [contacts]
   (reduce
    (fn [acc [key {:keys [alias name identicon public-key] :as contact}]]
      (println :foo alias (contact.db/blocked? contact))
      (if (and alias
               (not= alias "")
               (not (contact.db/blocked? contact)))
        (let [name (utils/safe-replace name ".stateofus.eth" "")]
          (assoc acc public-key
                 {:alias      alias
                  :name       (or name alias)
                  :identicon  identicon
                  :public-key key}))
        acc))
    {}
    contacts)))

(re-frame/reg-sub
 :chats/mentionable-users
 :<- [:chats/current-chat]
 :<- [:chats/mentionable-contacts]
 :<- [:contacts/blocked-set]
 :<- [:multiaccount]
 (fn [[{:keys [users]} contacts blocked {:keys [name preferred-name photo-path public-key]}]]
   (apply dissoc
          (-> users
              (merge contacts)
              (assoc public-key {:alias      name
                                 :name       (or preferred-name name)
                                 :identicon  photo-path
                                 :public-key public-key}))
          blocked)))

(re-frame/reg-sub
 :chat/mention-suggestions
 :<- [:chats/current-chat-id]
 :<- [:chats/mention-suggestions]
 (fn [[chat-id mentions]]
   (take 15 (get mentions chat-id))))

(re-frame/reg-sub
 :chat/cursor
 :<- [:chats/current-chat-id]
 :<- [:chats/cursor]
 (fn [[chat-id cursor]]
   (get cursor chat-id)))

(re-frame/reg-sub
 :chat/input-with-mentions
 :<- [:chats/current-chat-id]
 :<- [:chats/input-with-mentions]
 (fn [[chat-id cursor]]
   (get cursor chat-id)))

;;BOOTNODES ============================================================================================================

(re-frame/reg-sub
 :custom-bootnodes/enabled?
 :<- [:multiaccount]
 :<- [:networks/current-network]
 (fn [[{:keys [custom-bootnodes-enabled?]} current-network]]
   (get custom-bootnodes-enabled? current-network)))

(re-frame/reg-sub
 :custom-bootnodes/network-bootnodes
 :<- [:multiaccount]
 :<- [:networks/current-network]
 (fn [[multiaccount current-network]]
   (get-in multiaccount [:custom-bootnodes current-network])))

(re-frame/reg-sub
 :get-manage-bootnode
 :<- [:bootnodes/manage]
 (fn [manage]
   manage))

(re-frame/reg-sub
 :manage-bootnode-validation-errors
 :<- [:get-manage-bootnode]
 (fn [manage]
   (set (keep
         (fn [[k {:keys [error]}]]
           (when error k))
         manage))))

;;BROWSER ==============================================================================================================

(re-frame/reg-sub
 :browser/browsers
 :<- [:browsers]
 (fn [browsers]
   (reduce (fn [acc [k browser]]
             (update acc k assoc :url (browser/get-current-url browser)))
           browsers
           browsers)))

(re-frame/reg-sub
 :browser/browsers-vals
 :<- [:browser/browsers]
 (fn [browsers]
   (sort-by :timestamp > (vals browsers))))

(re-frame/reg-sub
 :get-current-browser
 :<- [:browser/options]
 :<- [:browser/browsers]
 (fn [[options browsers]]
   (let [browser (get browsers (:browser-id options))]
     (assoc browser :secure? (browser/secure? browser options)))))

;;STICKERS =============================================================================================================

(re-frame/reg-sub
 :stickers/installed-packs-vals
 :<- [:stickers/installed-packs]
 (fn [packs]
   (vals packs)))

(re-frame/reg-sub
 :stickers/all-packs
 :<- [:stickers/packs]
 :<- [:stickers/installed-packs]
 :<- [:stickers/packs-owned]
 :<- [:stickers/packs-pending]
 (fn [[packs installed owned pending]]
   (map (fn [{:keys [id] :as pack}]
          (cond-> pack
            (get installed id) (assoc :installed true)
            (get owned id) (assoc :owned true)
            (get pending id) (assoc :pending true)))
        (vals packs))))

(re-frame/reg-sub
 :stickers/get-current-pack
 :<- [:get-screen-params]
 :<- [:stickers/all-packs]
 (fn [[{:keys [id]} packs]]
   (first (filter #(= (:id %) id) packs))))

(defn find-pack-id-for-hash [sticker-uri packs]
  (some (fn [{:keys [stickers id]}]
          (when (some #(= sticker-uri (:hash %)) stickers)
            id))
        packs))

(re-frame/reg-sub
 :stickers/recent
 :<- [:multiaccount]
 :<- [:stickers/installed-packs-vals]
 (fn [[{:keys [:stickers/recent-stickers]} packs]]
   (map (fn [hash] {:hash hash :pack (find-pack-id-for-hash hash packs)}) recent-stickers)))

(re-frame/reg-sub
 :home-items
 :<- [:search/home-filter]
 :<- [:search/filtered-chats]
 (fn [[search-filter filtered-chats]]
   {:search-filter search-filter
    :chats         filtered-chats}))

;;PAIRING ==============================================================================================================


(re-frame/reg-sub
 :pairing/installations
 :<- [:get-pairing-installations]
 :<- [:pairing/installation-id]
 (fn [[installations installation-id]]
   (->> installations
        vals
        (pairing/sort-installations installation-id))))

(re-frame/reg-sub
 :pairing/installation-id
 :<- [:multiaccount]
 :installation-id)

(re-frame/reg-sub
 :pairing/installation-name
 :<- [:multiaccount]
 (fn [multiaccount] (:installation-name multiaccount)))

;;PROFILE ==============================================================================================================

(re-frame/reg-sub
 :get-profile-unread-messages-number
 :<- [:multiaccount]
 (fn [{:keys [mnemonic]}]
   (if mnemonic 1 0)))

;;WALLET ==============================================================================================================

(re-frame/reg-sub
 :balance
 :<- [:wallet]
 (fn [wallet [_ address]]
   (get-in wallet [:accounts address :balance])))

(re-frame/reg-sub
 :balance-default
 :<- [:wallet]
 :<- [:multiaccount/accounts]
 (fn [[wallet accounts]]
   (get-in wallet [:accounts (:address (ethereum/get-default-account accounts)) :balance])))

(re-frame/reg-sub
 :balances
 :<- [:wallet]
 (fn [wallet]
   (map :balance (vals (:accounts wallet)))))

(re-frame/reg-sub
 :empty-balances?
 :<- [:balances]
 (fn [balances]
   (every?
    (fn [balance]
      (every?
       (fn [^js asset]
         (or (nil? asset) (.isZero asset)))
       (vals balance)))
    balances)))

(re-frame/reg-sub
 :price
 :<- [:prices]
 (fn [prices [_ fsym tsym]]
   (get-in prices [fsym tsym :price])))

(re-frame/reg-sub
 :last-day
 :<- [:prices]
 (fn [prices [_ fsym tsym]]
   (get-in prices [fsym tsym :last-day])))

(re-frame/reg-sub
 :wallet.settings/currency
 :<- [:multiaccount]
 (fn [settings]
   (or (get settings :currency) :usd)))

(defn- get-balance-total-value
  [balance prices currency token->decimals]
  (reduce-kv (fn [acc symbol value]
               (if-let [price (get-in prices [symbol currency :price])]
                 (+ acc (or (some-> (money/internal->formatted value symbol (token->decimals symbol))
                                    ^js (money/crypto->fiat price)
                                    .toNumber)
                            0))
                 acc)) 0 balance))

(re-frame/reg-sub
 :wallet/token->decimals
 :<- [:wallet/all-tokens]
 (fn [all-tokens]
   (into {} (map #(vector (:symbol %) (:decimals %)) (vals all-tokens)))))

(re-frame/reg-sub
 :portfolio-value
 :<- [:balances]
 :<- [:prices]
 :<- [:wallet/currency]
 :<- [:wallet/token->decimals]
 (fn [[balances prices currency token->decimals]]
   (if (and balances prices)
     (let [currency-key        (-> currency :code keyword)
           balance-total-value (apply + (map #(get-balance-total-value % prices currency-key token->decimals) balances))]
       (if (pos? balance-total-value)
         (-> balance-total-value
             (money/with-precision 2)
             str
             (i18n/format-currency (:code currency)))
         "0"))
     "...")))

(re-frame/reg-sub
 :account-portfolio-value
 (fn [[_ address] _]
   [(re-frame/subscribe [:balance address])
    (re-frame/subscribe [:prices])
    (re-frame/subscribe [:wallet/currency])
    (re-frame/subscribe [:wallet/token->decimals])])
 (fn [[balance prices currency token->decimals]]
   (if (and balance prices)
     (let [currency-key        (-> currency :code keyword)
           balance-total-value (get-balance-total-value balance prices currency-key token->decimals)]
       (if (pos? balance-total-value)
         (-> balance-total-value
             (money/with-precision 2)
             str
             (i18n/format-currency (:code currency)))
         "0"))
     "...")))

(re-frame/reg-sub
 :wallet/sorted-tokens
 :<- [:wallet/all-tokens]
 (fn [all-tokens]
   (tokens/sorted-tokens-for all-tokens)))

(re-frame/reg-sub
 :wallet/grouped-chain-tokens
 :<- [:wallet/sorted-tokens]
 :<- [:wallet/visible-tokens-symbols]
 (fn [[all-tokens visible-tokens]]
   (let [vt-set (set visible-tokens)]
     (group-by :custom? (map #(assoc % :checked? (boolean (get vt-set (keyword (:symbol %))))) all-tokens)))))

(re-frame/reg-sub
 :wallet/fetching-tx-history?
 :<- [:wallet]
 (fn [wallet [_ address]]
   (get-in wallet [:fetching address :history?])))

(re-frame/reg-sub
 :wallet/fetching-recent-tx-history?
 :<- [:wallet]
 (fn [wallet [_ address]]
   (get-in wallet [:fetching address :recent?])))

(re-frame/reg-sub
 :wallet/tx-history-fetched?
 :<- [:wallet]
 (fn [wallet [_ address]]
   (get-in wallet [:fetching address :all-fetched?])))

(re-frame/reg-sub
 :wallet/etherscan-link
 (fn [db [_ address]]
   (let [network (:networks/current-network db)
         link    (get-in constants/default-networks-by-id
                         [network :etherscan-link])]
     (when link
       (str link address)))))

(re-frame/reg-sub
 :wallet/error-message
 :<- [:wallet]
 (fn [wallet]
   (or (get-in wallet [:errors :balance-update])
       (get-in wallet [:errors :prices-update]))))

(re-frame/reg-sub
 :wallet/visible-tokens-symbols
 :<- [:ethereum/chain-keyword]
 :<- [:multiaccount]
 (fn [[chain current-multiaccount]]
   (get-in current-multiaccount [:wallet/visible-tokens chain])))

(re-frame/reg-sub
 :wallet/visible-assets
 :<- [:ethereum/chain-keyword]
 :<- [:wallet/visible-tokens-symbols]
 :<- [:wallet/sorted-tokens]
 (fn [[chain visible-tokens-symbols all-tokens-sorted]]
   (conj (filter #(contains? visible-tokens-symbols (:symbol %)) all-tokens-sorted)
         (tokens/native-currency chain))))

(re-frame/reg-sub
 :wallet/visible-assets-with-amount
 (fn [[_ address] _]
   [(re-frame/subscribe [:balance address])
    (re-frame/subscribe [:wallet/visible-assets])])
 (fn [[balance visible-assets]]
   (map #(assoc % :amount (get balance (:symbol %))) visible-assets)))

(defn update-value [prices currency]
  (fn [{:keys [symbol decimals amount] :as token}]
    (let [price (get-in prices [symbol (-> currency :code keyword) :price])]
      (assoc token
             :price price
             :value (when (and amount price)
                      (-> (money/internal->formatted amount symbol decimals)
                          (money/crypto->fiat price)
                          (money/with-precision 2)
                          str
                          (i18n/format-currency (:code currency))))))))

(re-frame/reg-sub
 :wallet/visible-assets-with-values
 (fn [[_ address] _]
   [(re-frame/subscribe [:wallet/visible-assets-with-amount address])
    (re-frame/subscribe [:prices])
    (re-frame/subscribe [:wallet/currency])])
 (fn [[assets prices currency]]
   (let [{:keys [tokens nfts]} (group-by #(if (:nft? %) :nfts :tokens) assets)
         tokens-with-values (map (update-value prices currency) tokens)]
     {:tokens tokens-with-values
      :nfts   nfts})))

(defn get-asset-amount [balances sym]
  (reduce #(if-let [^js bl (get %2 sym)]
             (.plus ^js %1 bl)
             %1)
          ^js (money/bignumber 0)
          balances))

(re-frame/reg-sub
 :wallet/all-visible-assets-with-amount
 :<- [:balances]
 :<- [:wallet/visible-assets]
 (fn [[balances visible-assets]]
   (map #(assoc % :amount (get-asset-amount balances (:symbol %))) visible-assets)))

(re-frame/reg-sub
 :wallet/all-visible-assets-with-values
 :<- [:wallet/all-visible-assets-with-amount]
 :<- [:prices]
 :<- [:wallet/currency]
 (fn [[assets prices currency]]
   (let [{:keys [tokens nfts]} (group-by #(if (:nft? %) :nfts :tokens) assets)
         tokens-with-values (map (update-value prices currency) tokens)]
     {:tokens tokens-with-values
      :nfts   nfts})))

(re-frame/reg-sub
 :wallet/transferrable-assets-with-amount
 (fn [[_ address]]
   (re-frame/subscribe [:wallet/visible-assets-with-amount address]))
 (fn [all-assets]
   (filter #(not (:nft? %)) all-assets)))

(re-frame/reg-sub
 :wallet/currency
 :<- [:wallet.settings/currency]
 (fn [currency-id]
   (get constants/currencies currency-id)))

;;WALLET TRANSACTIONS ==================================================================================================

(re-frame/reg-sub
 :wallet/accounts
 :<- [:wallet]
 (fn [wallet]
   (get wallet :accounts)))

(re-frame/reg-sub
 :wallet/account-by-transaction-hash
 :<- [:wallet/accounts]
 (fn [accounts [_ hash]]
   (some (fn [[address account]]
           (when-let [transaction (get-in account [:transactions hash])]
             (assoc transaction :address address)))
         accounts)))

(re-frame/reg-sub
 :wallet/transactions
 :<- [:wallet]
 (fn [wallet [_ address]]
   (get-in wallet [:accounts address :transactions])))

(re-frame/reg-sub
 :wallet/filters
 :<- [:wallet]
 (fn [wallet]
   (get wallet :filters)))

(defn enrich-transaction
  [{:keys [type to from value token] :as transaction}
   contacts native-currency]
  (let [[contact-address key-contact key-wallet]
        (if (= type :inbound)
          [from :from-contact :to-wallet]
          [to :to-contact :from-wallet])
        wallet  (i18n/label :main-wallet)
        contact (get contacts contact-address)
        {:keys [symbol-display symbol decimals] :as asset}
        (or token native-currency)
        amount-text   (if value
                        (wallet.utils/format-amount value decimals)
                        "...")
        currency-text (when asset
                        (clojure.core/name (or symbol-display symbol)))]
    (cond-> transaction
      contact (assoc key-contact (:name contact))
      :always (assoc key-wallet wallet
                     :amount-text    amount-text
                     :currency-text  currency-text))))

(re-frame/reg-sub
 :wallet.transactions/transactions
 (fn [[_ address] _]
   [(re-frame/subscribe [:wallet/transactions address])
    (re-frame/subscribe [:contacts/contacts-by-address])
    (re-frame/subscribe [:ethereum/native-currency])])
 (fn [[transactions contacts native-currency]]
   (reduce (fn [acc [hash transaction]]
             (assoc acc
                    hash
                    (enrich-transaction transaction contacts native-currency))) ;;TODO this doesn't look good for performance, we need to calculate this only once for each transaction
           {}
           transactions)))

(re-frame/reg-sub
 :wallet.transactions/all-filters?
 :<- [:wallet/filters]
 (fn [filters]
   (= wallet.db/default-wallet-filters
      filters)))

(def filters-labels
  {:inbound  (i18n/label :t/incoming)
   :outbound (i18n/label :t/outgoing)
   :pending  (i18n/label :t/pending)
   :failed   (i18n/label :t/failed)})

(re-frame/reg-sub
 :wallet.transactions/filters
 :<- [:wallet/filters]
 (fn [filters]
   (map (fn [id]
          (let [checked? (filters id)]
            {:id id
             :label (filters-labels id)
             :checked? checked?
             :on-touch #(if checked?
                          (re-frame/dispatch [:wallet.transactions/remove-filter id])
                          (re-frame/dispatch [:wallet.transactions/add-filter id]))}))
        wallet.db/default-wallet-filters)))

(re-frame/reg-sub
 :wallet.transactions.filters/screen
 :<- [:wallet.transactions/filters]
 :<- [:wallet.transactions/all-filters?]
 (fn [[filters all-filters?]]
   {:all-filters? all-filters?
    :filters filters
    :on-touch-select-all (when-not all-filters?
                           #(re-frame/dispatch
                             [:wallet.transactions/add-all-filters]))}))

(defn- enrich-transaction-for-list
  [filters
   {:keys [type from-contact from to-contact to hash timestamp] :as transaction}
   address]
  (when (filters type)
    (assoc (case type
             :inbound
             (assoc transaction
                    :label (i18n/label :t/from)
                    :contact-accessibility-label :sender-text
                    :address-accessibility-label :sender-address-text
                    :contact from-contact
                    :address from)
             (assoc transaction
                    :label (i18n/label :t/to)
                    :contact-accessibility-label :recipient-name-text
                    :address-accessibility-label :recipient-address-text
                    :contact to-contact
                    :address to))
           :time-formatted (datetime/timestamp->time timestamp)
           :on-touch-fn #(re-frame/dispatch [:wallet.ui/show-transaction-details hash address]))))

(defn- group-transactions-by-date
  [transactions]
  (->> transactions
       (group-by #(datetime/timestamp->date-key (:timestamp %)))
       (sort-by key >)
       (map (fn [[date-key transactions]]
              {:title (datetime/timestamp->mini-date (:timestamp (first transactions)))
               :key   date-key
               :data  (sort-by :timestamp > transactions)}))))

(re-frame/reg-sub
 :wallet.transactions.history/screen
 (fn [[_ address] _]
   [(re-frame/subscribe [:wallet.transactions/transactions address])
    (re-frame/subscribe [:wallet/filters])
    (re-frame/subscribe [:wallet.transactions/all-filters?])])
 (fn [[transactions filters all-filters?] [_ address]]
   {:all-filters? all-filters?
    :transaction-history-sections
    (->> transactions
         vals
         (keep #(enrich-transaction-for-list filters % address))
         (group-transactions-by-date))}))

(re-frame/reg-sub
 :wallet.transactions.details/current-transaction
 (fn [[_ _ address] _]
   [(re-frame/subscribe [:wallet.transactions/transactions address])
    (re-frame/subscribe [:ethereum/native-currency])
    (re-frame/subscribe [:ethereum/chain-keyword])])
 (fn [[transactions native-currency chain-keyword] [_ hash _]]
   (let [{:keys [gas-used gas-price hash timestamp type]
          :as transaction}
         (get transactions hash)
         native-currency-text (name (or (:symbol-display native-currency)
                                        (:symbol native-currency)))]
     (when transaction
       (merge transaction
              {:gas-price-eth  (if gas-price
                                 (money/wei->str :eth
                                                 gas-price
                                                 native-currency-text)
                                 "-")
               :gas-price-gwei (if gas-price
                                 (money/wei->str :gwei
                                                 gas-price)
                                 "-")
               :date           (datetime/timestamp->long-date timestamp)}
              (if (= type :unsigned)
                {:block     (i18n/label :not-applicable)
                 :cost      (i18n/label :not-applicable)
                 :gas-limit (i18n/label :not-applicable)
                 :gas-used  (i18n/label :not-applicable)
                 :nonce     (i18n/label :not-applicable)
                 :hash      (i18n/label :not-applicable)}
                {:cost (when gas-used
                         (money/wei->str :eth
                                         (money/fee-value gas-used gas-price)
                                         native-currency-text))
                 :url  (transactions/get-transaction-details-url
                        chain-keyword
                        hash)}))))))

(re-frame/reg-sub
 :wallet.transactions.details/screen
 (fn [[_ hash address] _]
   [(re-frame/subscribe [:wallet.transactions.details/current-transaction hash address])
    (re-frame/subscribe [:ethereum/current-block])])
 (fn [[transaction current-block]]
   (let [confirmations (wallet.db/get-confirmations transaction
                                                    current-block)]
     (assoc transaction
            :confirmations confirmations
            :confirmations-progress
            (if (>= confirmations transactions/confirmations-count-threshold)
              100
              (* 100 (/ confirmations transactions/confirmations-count-threshold)))))))

;;WALLET SEND ==========================================================================================================

(re-frame/reg-sub
 ::send-transaction
 :<- [:wallet]
 (fn [wallet]
   (:send-transaction wallet)))

(re-frame/reg-sub
 :wallet.send/symbol
 :<- [::send-transaction]
 (fn [send-transaction]
   (:symbol send-transaction)))

(re-frame/reg-sub
 :wallet.send/camera-flashlight
 :<- [::send-transaction]
 (fn [send-transaction]
   (:camera-flashlight send-transaction)))

(re-frame/reg-sub
 :wallet/settings
 :<- [:wallet]
 (fn [{:keys [settings]}]
   (reduce-kv #(conj %1 %3) [] settings)))

(re-frame/reg-sub
 :wallet.request/transaction
 :<- [:wallet]
 :request-transaction)

(re-frame/reg-sub
 :screen-collectibles
 :<- [:collectibles]
 :<- [:get-screen-params]
 (fn [[collectibles {:keys [symbol]}]]
   (when-let [v (get collectibles symbol)]
     (mapv #(assoc (second %) :id (first %)) v))))

;;UI ==============================================================================================================

;;TODO this subscription looks super weird huge and with dispatches?
(re-frame/reg-sub
 :connectivity/status-properties
 :<- [:network-status]
 :<- [:disconnected?]
 :<- [:mailserver/connecting?]
 :<- [:mailserver/connection-error?]
 :<- [:mailserver/request-error?]
 :<- [:mailserver/fetching?]
 :<- [:network/type]
 :<- [:multiaccount]
 (fn [[network-status disconnected? mailserver-connecting? mailserver-connection-error?
       mailserver-request-error? mailserver-fetching? network-type multiaccount]]
   (let [error-label     (cond
                           (= network-status :offline)
                           :t/offline

                           mailserver-connecting?
                           :t/connecting

                           mailserver-connection-error?
                           :t/mailserver-reconnect

                           mailserver-request-error?
                           :t/mailserver-request-error-status

                           (and (mobile-network-utils/cellular? network-type)
                                (not (:syncing-on-mobile-network? multiaccount)))
                           :mobile-network

                           disconnected?
                           :t/offline

                           :else nil)
         connected?       (and (nil? error-label) (not= :mobile-network error-label))]
     {:message            (or error-label :t/connected)
      :connected?         connected?
      :connecting?        (= error-label :t/connecting)
      :loading-indicator? (and mailserver-fetching? connected?)
      :on-press-event       (cond
                              mailserver-connection-error?
                              :mailserver.ui/reconnect-mailserver-pressed

                              mailserver-request-error?
                              :mailserver.ui/request-error-pressed

                              (= :mobile-network error-label)
                              :mobile-network/show-offline-sheet)})))

;;CONTACT ==============================================================================================================

(re-frame/reg-sub
 ::query-current-chat-contacts
 :<- [:chats/current-chat]
 :<- [:contacts/contacts]
 (fn [[chat contacts] [_ query-fn]]
   (contact.db/query-chat-contacts chat contacts query-fn)))

(re-frame/reg-sub
 :contacts/contacts
 :<- [::contacts]
 (fn [contacts]
   (contact.db/enrich-contacts contacts)))

(re-frame/reg-sub
 :contacts/active
 :<- [:contacts/contacts]
 (fn [contacts]
   (contact.db/get-active-contacts contacts)))

(re-frame/reg-sub
 :contacts/active-count
 :<- [:contacts/active]
 (fn [active-contacts]
   (count active-contacts)))

(re-frame/reg-sub
 :contacts/blocked
 :<- [:contacts/contacts]
 (fn [contacts]
   (->> contacts
        (filter (fn [[_ contact]]
                  (contact.db/blocked? contact)))
        (contact.db/sort-contacts))))

(re-frame/reg-sub
 :contacts/blocked-count
 :<- [:contacts/blocked]
 (fn [blocked-contacts]
   (count blocked-contacts)))

(re-frame/reg-sub
 :contacts/current-contact
 :<- [:contacts/contacts]
 :<- [:contacts/current-contact-identity]
 (fn [[contacts identity]]
   (or (get contacts identity)
       (-> identity
           contact.db/public-key->new-contact
           contact.db/enrich-contact))))

(re-frame/reg-sub
 :contacts/contact-by-identity
 :<- [:contacts/contacts]
 (fn [contacts [_ identity]]
   (or (get contacts identity)
       (multiaccounts/contact-with-names {:public-key identity}))))

(re-frame/reg-sub
 :contacts/contact-added?
 (fn [[_ identity] _]
   [(re-frame/subscribe [:contacts/contact-by-identity identity])])
 (fn [[contact] _]
   (contact.db/added? contact)))

(re-frame/reg-sub
 :contacts/contact-two-names-by-identity
 (fn [[_ identity] _]
   [(re-frame/subscribe [:contacts/contact-by-identity identity])
    (re-frame/subscribe [:multiaccount])])
 (fn [[contact current-multiaccount] [_ identity]]
   (let [me? (= (:public-key current-multiaccount) identity)]
     (if me?
       [(or (:preferred-name current-multiaccount)
            (gfycat/generate-gfy identity))]
       (multiaccounts/contact-two-names contact false)))))

(re-frame/reg-sub
 :contacts/contact-name-by-identity
 (fn [[_ identity] _]
   [(re-frame/subscribe [:contacts/contact-two-names-by-identity identity])])
 (fn [[names] _]
   (first names)))

(re-frame/reg-sub
 :messages/quote-info
 :<- [:chats/messages]
 :<- [:contacts/contacts]
 :<- [:multiaccount]
 (fn [[messages contacts current-multiaccount] [_ message-id]]
   (when-let [message (get messages message-id)]
     (let [identity (:from message)
           me? (= (:public-key current-multiaccount) identity)]
       (if me?
         {:quote       {:from  identity
                        :text (get-in message [:content :text])}
          :ens-name (:preferred-name current-multiaccount)
          :alias (gfycat/generate-gfy identity)}
         (let [contact (or (contacts identity)
                           (contact.db/public-key->new-contact identity))]
           {:quote     {:from  identity
                        :text (get-in message [:content :text])}
            :ens-name  (when (:ens-verified contact)
                         (:name contact))
            :alias (or (:alias contact)
                       (gfycat/generate-gfy identity))}))))))

(re-frame/reg-sub
 :contacts/all-contacts-not-in-current-chat
 :<- [::query-current-chat-contacts remove]
 (fn [contacts]
   (->> contacts
        (filter contact.db/added?))))

(re-frame/reg-sub
 :contacts/current-chat-contacts
 :<- [:chats/current-chat]
 :<- [:contacts/contacts]
 :<- [:multiaccount]
 (fn [[{:keys [contacts admins]} all-contacts current-multiaccount]]
   (contact.db/get-all-contacts-in-group-chat contacts admins all-contacts current-multiaccount)))

(re-frame/reg-sub
 :contacts/contacts-by-chat
 (fn [[_ _ chat-id] _]
   [(re-frame/subscribe [:chats/chat chat-id])
    (re-frame/subscribe [:contacts/contacts])])
 (fn [[chat all-contacts] [_ query-fn]]
   (contact.db/query-chat-contacts chat all-contacts query-fn)))

(re-frame/reg-sub
 :contacts/contact-by-address
 :<- [:contacts/contacts]
 (fn [contacts [_ address]]
   (contact.db/find-contact-by-address contacts address)))

(re-frame/reg-sub
 :contacts/contacts-by-address
 :<- [:contacts/contacts]
 (fn [contacts]
   (reduce (fn [acc [_ {:keys [address] :as contact}]]
             (if address
               (assoc acc address contact)
               acc))
           {}
           contacts)))

;;MAILSERVER ===========================================================================================================

(re-frame/reg-sub
 :mailserver/connecting?
 :<- [:mailserver/state]
 (fn [state]
   (#{:connecting :added} state)))

(re-frame/reg-sub
 :mailserver/connection-error?
 :<- [:mailserver/state]
 (fn [state]
   (#{:error :disconnected} state)))

(re-frame/reg-sub
 :chats/fetching-gap-in-progress?
 :<- [:chats/current-chat-id]
 :<- [:mailserver/fetching-gaps-in-progress]
 (fn [[chat-id gaps] [_ ids]]
   (seq (select-keys (get gaps chat-id) ids))))

(re-frame/reg-sub
 :mailserver/fetching?
 :<- [:mailserver/state]
 :<- [:mailserver/pending-requests]
 :<- [:mailserver/connecting?]
 :<- [:mailserver/connection-error?]
 :<- [:mailserver/request-error?]
 (fn [[state pending-requests connecting? connection-error? request-error?]]
   (and pending-requests
        (= state :connected)
        (pos-int? pending-requests)
        (not (or connecting? connection-error? request-error?)))))

(re-frame/reg-sub
 :mailserver/fleet-mailservers
 :<- [:fleets/current-fleet]
 :<- [:mailserver/mailservers]
 (fn [[current-fleet mailservers]]
   (current-fleet mailservers)))

(re-frame/reg-sub
 :mailserver.edit/connected?
 :<- [:mailserver.edit/mailserver]
 :<- [:mailserver/current-id]
 (fn [[mailserver current-mailserver-id]]
   (= (get-in mailserver [:id :value])
      current-mailserver-id)))

(re-frame/reg-sub
 :mailserver.edit/validation-errors
 :<- [:mailserver.edit/mailserver]
 (fn [mailserver]
   (set (keep
         (fn [[k {:keys [error]}]]
           (when error k))
         mailserver))))

(re-frame/reg-sub
 :mailserver/connected?
 :<- [:mailserver/state]
 :<- [:disconnected?]
 (fn [[mail-state disconnected?]]
   (let [mailserver-connected? (= :connected mail-state)]
     (and mailserver-connected?
          (not disconnected?)))))

(re-frame/reg-sub
 :mailserver/preferred-id
 :<- [:multiaccount]
 (fn [multiaccount]
   (get-in multiaccount
           [:pinned-mailservers (fleet/current-fleet-sub multiaccount)])))

;;SEARCH ==============================================================================================================

(defn extract-chat-attributes [chat]
  (let [{:keys [name alias tags]} (val chat)]
    (into [name alias] tags)))

(defn sort-by-timestamp
  [coll]
  (when (not-empty coll)
    (sort-by #(-> % second :timestamp) >
             (into {} coll))))

(defn apply-filter
  "extract-attributes-fn is a function that take an element from the collection
  and returns a vector of attributes which are strings
  apply-filter returns the elements for which at least one attribute includes
  the search-filter
  apply-filter returns nil if there is no element that match the filter
  apply-filter returns full collection if the search-filter is empty"
  [search-filter coll extract-attributes-fn sort?]
  (let [results (if (not-empty search-filter)
                  (let [search-filter (string/lower-case search-filter)]
                    (filter (fn [element]
                              (some (fn [v]
                                      (let [s (cond (string? v) v
                                                    (keyword? v) (name v))]
                                        (when (string? s)
                                          (string/includes? (string/lower-case s)
                                                            search-filter))))
                                    (extract-attributes-fn element)))
                            coll))
                  coll)]
    (if sort?
      (sort-by-timestamp results)
      results)))

(defn filter-chat
  [contacts search-filter {:keys [group-chat alias name chat-id]}]
  (let [alias (if-not group-chat
                (string/lower-case (or alias
                                       (get-in contacts [chat-id :alias])
                                       (gfycat/generate-gfy chat-id)))
                "")
        nickname (get-in contacts [chat-id :nickname])]
    (or
     (string/includes? (string/lower-case (str name)) search-filter)
     (string/includes? (string/lower-case alias) search-filter)
     (when nickname
       (string/includes? (string/lower-case nickname) search-filter))
     (and
      (get-in contacts [chat-id :ens-verified])
      (string/includes? (string/lower-case
                         (str (get-in contacts [chat-id :name])))
                        search-filter)))))

(re-frame/reg-sub
 :search/filtered-chats
 :<- [:chats/active-chats]
 :<- [::contacts]
 :<- [:search/home-filter]
 (fn [[chats contacts search-filter]]
   ;; Short-circuit if search-filter is empty
   (let [filtered-chats (if (seq search-filter)
                          (filter
                           (partial filter-chat
                                    contacts
                                    (string/lower-case search-filter))
                           (vals chats))
                          (vals chats))]

     (sort-by :timestamp > filtered-chats))))

(defn extract-currency-attributes [currency]
  (let [{:keys [code display-name]} (val currency)]
    [code display-name]))

(re-frame/reg-sub
 :search/filtered-currencies
 :<- [:search/currency-filter]
 (fn [search-currency-filter]
   {:search-filter search-currency-filter
    :currencies (apply-filter search-currency-filter constants/currencies extract-currency-attributes false)}))

(defn extract-token-attributes [token]
  (let [{:keys [symbol name]} token]
    [symbol name]))

(re-frame/reg-sub
 :wallet/filtered-grouped-chain-tokens
 :<- [:wallet/grouped-chain-tokens]
 :<- [:search/token-filter]
 (fn [[{custom-tokens true default-tokens nil} search-token-filter]]
   {:search-filter search-token-filter
    :tokens {true (apply-filter search-token-filter custom-tokens extract-token-attributes false)
             nil (apply-filter search-token-filter default-tokens extract-token-attributes false)}}))

;; TRIBUTE TO TALK
(re-frame/reg-sub
 :tribute-to-talk/settings
 :<- [:multiaccount]
 :<- [:ethereum/chain-keyword]
 (fn [[multiaccount chain-keyword]]
   (get-in multiaccount [:tribute-to-talk]) chain-keyword))

(re-frame/reg-sub
 :tribute-to-talk/screen-params
 :<- [:screen-params]
 (fn [screen-params]
   (get screen-params :tribute-to-talk)))

(re-frame/reg-sub
 :tribute-to-talk/profile
 :<- [:tribute-to-talk/settings]
 :<- [:tribute-to-talk/screen-params]
 (fn [[{:keys [seen? snt-amount]}
       {:keys [state unavailable?]}]]
   (let [state (or state (if snt-amount :completed :disabled))
         snt-amount (tribute-to-talk.db/from-wei snt-amount)]
     (when config/tr-to-talk-enabled?
       (if unavailable?
         {:subtext "Change network to enable Tribute to Talk"
          :active? false
          :icon :main-icons/tribute-to-talk
          :icon-color colors/gray}
         (cond-> {:new? (not seen?)}
           (and (not (and seen?
                          snt-amount
                          (#{:signing :pending :transaction-failed :completed} state))))
           (assoc :subtext (i18n/label :t/tribute-to-talk-desc))

           (#{:signing :pending} state)
           (assoc :activity-indicator {:animating true
                                       :color colors/blue}
                  :subtext (case state
                             :pending (i18n/label :t/pending-confirmation)
                             :signing (i18n/label :t/waiting-to-sign)))

           (= state :transaction-failed)
           (assoc :icon :main-icons/warning
                  :icon-color colors/red
                  :subtext (i18n/label :t/transaction-failed))

           (not (#{:signing :pending :transaction-failed} state))
           (assoc :icon :main-icons/tribute-to-talk)

           (and (= state :completed)
                (not-empty snt-amount))
           (assoc :accessory-value (str snt-amount " SNT"))))))))

(re-frame/reg-sub
 :tribute-to-talk/enabled?
 :<- [:tribute-to-talk/settings]
 (fn [settings]
   (tribute-to-talk.db/enabled? settings)))

(re-frame/reg-sub
 :tribute-to-talk/settings-ui
 :<- [:tribute-to-talk/settings]
 :<- [:tribute-to-talk/screen-params]
 :<- [:prices]
 :<- [:wallet/currency]
 (fn [[{:keys [seen? snt-amount message]
        :as settings}
       {:keys [step editing? state error]
        :or {step :intro}
        screen-snt-amount :snt-amount}
       prices currency]]
   (let [fiat-value (if snt-amount
                      (money/fiat-amount-value
                       snt-amount
                       :SNT
                       (-> currency :code keyword)
                       prices)
                      "0")]
     (cond-> {:seen? seen?
              :snt-amount (tribute-to-talk.db/from-wei snt-amount)
              :message message
              :enabled? (tribute-to-talk.db/enabled? settings)
              :error error
              :step step
              :state (or state (if snt-amount :completed :disabled))
              :editing? editing?
              :fiat-value (str fiat-value " " (:code currency))}

       (= step :set-snt-amount)
       (assoc :snt-amount (str screen-snt-amount)
              :disable-button?
              (boolean (and (= step :set-snt-amount)
                            (or (string/blank? screen-snt-amount)
                                (#{"0" "0.0" "0.00"} screen-snt-amount)
                                (string/ends-with? screen-snt-amount ".")))))))))

;;ENS ==================================================================================================================

(re-frame/reg-sub
 :ens.stateofus/registrar
 :<- [:current-network]
 (fn [network]
   (let [chain (ethereum/network->chain-keyword network)]
     (get stateofus/registrars chain))))

(re-frame/reg-sub
 :multiaccount/usernames
 :<- [:multiaccount]
 (fn [multiaccount]
   (:usernames multiaccount)))

(re-frame/reg-sub
 :ens/preferred-name
 :<- [:multiaccount]
 (fn [multiaccount]
   (:preferred-name multiaccount)))

(re-frame/reg-sub
 :ens/search-screen
 :<- [:ens/registration]
 (fn [{:keys [custom-domain? username state]}]
   {:state          state
    :username       username
    :custom-domain? custom-domain?}))

(defn- ens-amount-label
  [chain-id]
  (str (ens/registration-cost chain-id)
       (case chain-id
         3 " STT"
         1 " SNT"
         "")))

(re-frame/reg-sub
 :ens/checkout-screen
 :<- [:ens/registration]
 :<- [:ens.stateofus/registrar]
 :<- [:multiaccount/default-account]
 :<- [:multiaccount/public-key]
 :<- [:chain-id]
 :<- [:balance-default]
 (fn [[{:keys [custom-domain? username]}
       registrar default-account public-key chain-id balance]]
   {:address           (ethereum/normalized-hex (:address default-account))
    :username          username
    :public-key        public-key
    :custom-domain?    custom-domain?
    :contract          registrar
    :amount-label      (ens-amount-label chain-id)
    :sufficient-funds? (money/sufficient-funds?
                        (money/formatted->internal (money/bignumber 10) :SNT 18)
                        (get balance :SNT))}))

(re-frame/reg-sub
 :ens/confirmation-screen
 :<- [:ens/registration]
 (fn [{:keys [username state]}]
   {:state          state
    :username       username}))

(re-frame/reg-sub
 :ens.name/screen
 :<- [:get-screen-params :ens-name-details]
 :<- [:ens/names]
 (fn [[name ens]]
   (let [{:keys [address public-key expiration-date releasable?]} (get ens name)
         pending? (nil? address)]
     (cond-> {:name       name
              :custom-domain? (not (string/ends-with? name ".stateofus.eth"))}
       pending?
       (assoc :pending? true)
       (not pending?)
       (assoc :address    address
              :public-key public-key
              :releasable? releasable?
              :expiration-date expiration-date)))))

(re-frame/reg-sub
 :ens.main/screen
 :<- [:multiaccount/usernames]
 :<- [:multiaccount]
 :<- [:ens/preferred-name]
 :<- [:ens/registrations]
 (fn [[names multiaccount preferred-name registrations]]
   {:names             names
    :multiaccount      multiaccount
    :preferred-name    preferred-name
    :registrations registrations}))

;;SIGNING =============================================================================================================

(re-frame/reg-sub
 :signing/fee
 :<- [:signing/tx]
 (fn [{:keys [gas gasPrice]}]
   (signing.gas/calculate-max-fee gas gasPrice)))

(re-frame/reg-sub
 :signing/phrase
 :<- [:multiaccount]
 (fn [{:keys [signing-phrase]}]
   signing-phrase))

(re-frame/reg-sub
 :signing/sign-message
 :<- [:signing/sign]
 :<- [:multiaccount/accounts]
 :<- [:prices]
 (fn [[sign wallet-accounts prices]]
   (if (= :pinless (:type sign))
     (let [message (get-in sign [:formatted-data :message])
           wallet-acc (some #(when (= (:address %) (:receiver message)) %) wallet-accounts)]
       (cond-> sign
         (and (:amount message) (:currency message))
         (assoc :fiat-amount
                (money/fiat-amount-value (:amount message)
                                         (:currency message)
                                         :USD prices)
                :fiat-currency "USD")
         (and (:receiver message) wallet-acc)
         (assoc :account wallet-acc)))
     sign)))

(defn- too-precise-amount?
  "Checks if number has any extra digit beyond the allowed number of decimals.
  It does so by checking the number against its rounded value."
  [amount decimals]
  (let [^js bn (money/bignumber amount)]
    (not (.eq bn (.round bn decimals)))))

(defn get-amount-error [amount decimals]
  (when (and (seq amount) decimals)
    (let [normalized-amount (money/normalize amount)
          value             (money/bignumber normalized-amount)]
      (cond
        (not (money/valid? value))
        {:amount-error (i18n/label :t/validation-amount-invalid-number)}

        (too-precise-amount? normalized-amount decimals)
        {:amount-error (i18n/label :t/validation-amount-is-too-precise {:decimals decimals})}

        :else nil))))

(defn get-sufficient-funds-error
  [balance symbol amount]
  (when-not (money/sufficient-funds? amount (get balance symbol))
    {:amount-error (i18n/label :t/wallet-insufficient-funds)}))

(defn get-sufficient-gas-error
  [gas-error-message balance symbol amount ^js gas ^js gasPrice]
  (if (and gas gasPrice)
    (let [^js fee (.times gas gasPrice)
          ^js available-ether (money/bignumber (get balance :ETH 0))
          ^js available-for-gas (if (= :ETH symbol)
                                  (.minus available-ether (money/bignumber amount))
                                  available-ether)]
      (merge {:gas-error-state (when gas-error-message :gas-is-set)}
             (when-not (money/sufficient-funds? fee (money/bignumber available-for-gas))
               {:gas-error (i18n/label :t/wallet-insufficient-gas)})))
    {:gas-error-state (when gas-error-message :gas-isnt-set)
     :gas-error       (or gas-error-message (i18n/label :t/invalid-number))}))

(re-frame/reg-sub
 :signing/amount-errors
 (fn [[_ address] _]
   [(re-frame/subscribe [:signing/tx])
    (re-frame/subscribe [:balance address])])
 (fn [[{:keys [amount token gas gasPrice approve? gas-error-message]} balance]]
   (if (and amount token (not approve?))
     (let [amount-bn (money/formatted->internal (money/bignumber amount) (:symbol token) (:decimals token))
           amount-error (or (get-amount-error amount (:decimals token))
                            (get-sufficient-funds-error balance (:symbol token) amount-bn))]
       (merge amount-error (get-sufficient-gas-error gas-error-message balance (:symbol token) amount-bn gas gasPrice)))
     (get-sufficient-gas-error gas-error-message balance nil nil gas gasPrice))))

(re-frame/reg-sub
 :wallet.send/prepare-transaction-with-balance
 :<- [:wallet/prepare-transaction]
 :<- [:wallet]
 :<- [:offline?]
 :<- [:wallet/all-tokens]
 :<- [:ethereum/chain-keyword]
 (fn [[{:keys [symbol from to amount-text] :as transaction}
       wallet offline? all-tokens chain]]
   (let [balance (get-in wallet [:accounts (:address from) :balance])
         {:keys [decimals] :as token} (tokens/asset-for all-tokens chain symbol)
         {:keys [value error]} (wallet.db/parse-amount amount-text decimals)
         amount  (money/formatted->internal value symbol decimals)
         {:keys [amount-error] :as transaction-new}
         (merge transaction
                {:amount-error error}
                (when amount
                  (get-sufficient-funds-error balance symbol amount)))]
     (assoc transaction-new
            :amount amount
            :balance balance
            :token (assoc token :amount (get balance (:symbol token)))
            :sign-enabled? (and to
                                (nil? amount-error)
                                (not (nil? amount))
                                (not offline?))))))

(re-frame/reg-sub
 :wallet.request/prepare-transaction-with-balance
 :<- [:wallet/prepare-transaction]
 :<- [:wallet]
 :<- [:offline?]
 :<- [:wallet/all-tokens]
 :<- [:ethereum/chain-keyword]
 (fn [[{:keys [symbol from to amount-text] :as transaction}
       wallet offline? all-tokens chain]]
   (let [balance (get-in wallet [:accounts (:address from) :balance])
         {:keys [decimals] :as token} (tokens/asset-for all-tokens chain symbol)
         {:keys [value error]} (wallet.db/parse-amount amount-text decimals)
         amount  (money/formatted->internal value symbol decimals)
         {:keys [amount-error] :as transaction-new}
         (assoc transaction
                :amount-error error)]
     (assoc transaction-new
            :amount amount
            :balance balance
            :token (assoc token :amount (get balance (:symbol token)))
            :sign-enabled? (and to
                                from
                                (nil? amount-error)
                                (not (nil? amount))
                                (not offline?))))))

;; NETWORK SETTINGS

(defn- filter-networks [network-type]
  (fn [network]
    (let [chain-id (ethereum/network->chain-id network)
          testnet? (ethereum/testnet? chain-id)
          custom?  (:custom? network)]
      (case network-type
        :custom custom?
        :mainnet (and (not custom?) (not testnet?))
        :testnet (and (not custom?) testnet?)))))

(defn- label-networks [default-networks]
  (fn [network]
    (let [custom? (not (default-networks (:id network)))]
      (assoc network :custom? custom?))))

(re-frame/reg-sub
 :get-networks
 :<- [:networks/networks]
 (fn [networks]
   (let [networks (map (label-networks (into #{} (map :id constants/default-networks))) (sort-by :name (vals networks)))
         types    [:mainnet :testnet :custom]]
     (zipmap
      types
      (map #(filter (filter-networks %) networks) types)))))

(re-frame/reg-sub
 :manage-network-valid?
 :<- [:networks/manage]
 (fn [manage]
   (not-any? :error (vals manage))))
