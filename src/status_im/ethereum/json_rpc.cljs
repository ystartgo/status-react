(ns status-im.ethereum.json-rpc
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [status-im.ethereum.abi-spec :as abi-spec]
            [status-im.ethereum.decode :as decode]
            [status-im.native-module.core :as status]
            [status-im.utils.money :as money]
            [status-im.utils.types :as types]
            [status-im.utils.utils :as utils]
            [taoensso.timbre :as log]))

(def json-rpc-api
  {"eth_call" {}
   "eth_getBalance"
   {:on-result money/bignumber}
   "eth_estimateGas"
   {:on-result #(money/bignumber (if (= (int %) 21000) % (int (* % 1.2))))}
   "eth_gasPrice"
   {:on-result money/bignumber}
   "eth_getBlockByHash"
   {:on-result #(-> (update % :number decode/uint)
                    (update :timestamp decode/uint))}
   "eth_getTransactionByHash" {}
   "eth_getTransactionReceipt" {}
   "eth_getBlockByNumber" {}
   "eth_newBlockFilter" {:subscription? true}
   "eth_newFilter" {:subscription? true}
   "eth_syncing" {}
   "net_version" {}
   "web3_clientVersion" {}
   "shh_generateSymKeyFromPassword" {}
   "shh_getSymKey" {}
   "shh_markTrustedPeer" {}
   "waku_generateSymKeyFromPassword" {}
   "waku_getSymKey" {}
   "waku_markTrustedPeer" {}
   "shhext_post" {}
   "shhext_startMessenger" {}
   "shhext_sendPairInstallation" {}
   "shhext_syncDevices" {}
   "shhext_requestMessages" {}
   "shhext_sendDirectMessage" {}
   "shhext_sendPublicMessage" {}
   "shhext_enableInstallation" {}
   "shhext_disableInstallation" {}
   "shhext_sendChatMessage" {}
   "shhext_confirmJoiningGroup" {}
   "shhext_addAdminsToGroupChat" {}
   "shhext_addMembersToGroupChat" {}
   "shhext_removeMemberFromGroupChat" {}
   "shhext_leaveGroupChat" {}
   "shhext_changeGroupChatName" {}
   "shhext_createGroupChatWithMembers" {}
   "shhext_createGroupChatFromInvitation" {}
   "shhext_reSendChatMessage" {}
   "shhext_getOurInstallations" {}
   "shhext_setInstallationMetadata" {}
   "shhext_loadFilters" {}
   "shhext_loadFilter" {}
   "shhext_removeFilters" {}
   "shhext_sendContactUpdate" {}
   "shhext_sendContactUpdates" {}
   "shhext_chats" {}
   "shhext_addSystemMessages" {}
   "shhext_deleteMessagesFrom" {}
   "shhext_deleteMessagesByChatID" {}
   "shhext_deleteMessage" {}
   "shhext_markMessagesSeen" {}
   "shhext_markAllRead" {}
   "shhext_confirmMessagesProcessedByID" {}
   "shhext_updateMessageOutgoingStatus" {}
   "shhext_chatMessages" {}
   "shhext_saveChat" {}
   "shhext_muteChat" {}
   "shhext_unmuteChat" {}
   "shhext_contacts" {}
   "shhext_prepareContent" {}
   "shhext_blockContact" {}
   "shhext_updateMailservers" {}
   "shhext_sendEmojiReaction" {}
   "shhext_sendEmojiReactionRetraction" {}
   "shhext_emojiReactionsByChatID" {}
   ;;TODO not used anywhere?
   "shhext_deleteChat" {}
   "shhext_saveContact" {}
   "shhext_verifyENSNames" {}
   "shhext_requestAddressForTransaction" {}
   "shhext_requestTransaction" {}
   "shhext_acceptRequestAddressForTransaction" {}
   "shhext_declineRequestAddressForTransaction" {}
   "shhext_declineRequestTransaction" {}
   "shhext_sendTransaction" {}
   "shhext_acceptRequestTransaction" {}
   "shhext_signMessageWithChatKey" {}
   "shhext_sendGroupChatInvitationRequest" {}
   "shhext_sendGroupChatInvitationRejection" {}
   "shhext_getGroupChatInvitations" {}
   "shhext_registerForPushNotifications" {}
   "shhext_unregisterFromPushNotifications" {}
   "shhext_enablePushNotificationsFromContactsOnly" {}
   "shhext_disablePushNotificationsFromContactsOnly" {}
   "shhext_startPushNotificationsServer" {}
   "shhext_stopPushNotificationsServer" {}
   "shhext_disableSendingNotifications" {}
   "shhext_enableSendingNotifications" {}
   "shhext_addPushNotificationsServer" {}
   "shhext_getPushNotificationsServers" {}
   "shhext_enablePushNotificationsBlockMentions" {}
   "shhext_disablePushNotificationsBlockMentions" {}
   "wakuext_post" {}
   "wakuext_startMessenger" {}
   "wakuext_sendPairInstallation" {}
   "wakuext_syncDevices" {}
   "wakuext_requestMessages" {}
   "wakuext_sendDirectMessage" {}
   "wakuext_sendPublicMessage" {}
   "wakuext_enableInstallation" {}
   "wakuext_disableInstallation" {}
   "wakuext_sendChatMessage" {}
   "wakuext_confirmJoiningGroup" {}
   "wakuext_addAdminsToGroupChat" {}
   "wakuext_addMembersToGroupChat" {}
   "wakuext_removeMemberFromGroupChat" {}
   "wakuext_leaveGroupChat" {}
   "wakuext_changeGroupChatName" {}
   "wakuext_createGroupChatWithMembers" {}
   "wakuext_createGroupChatFromInvitation" {}
   "wakuext_reSendChatMessage" {}
   "wakuext_getOurInstallations" {}
   "wakuext_setInstallationMetadata" {}
   "wakuext_loadFilters" {}
   "wakuext_loadFilter" {}
   "wakuext_removeFilters" {}
   "wakuext_sendContactUpdate" {}
   "wakuext_sendContactUpdates" {}
   "wakuext_chats" {}
   "wakuext_addSystemMessages" {}
   "wakuext_deleteMessagesFrom" {}
   "wakuext_deleteMessagesByChatID" {}
   "wakuext_deleteMessage" {}
   "wakuext_markMessagesSeen" {}
   "wakuext_markAllRead" {}
   "wakuext_confirmMessagesProcessedByID" {}
   "wakuext_updateMessageOutgoingStatus" {}
   "wakuext_chatMessages" {}
   "wakuext_saveChat" {}
   "wakuext_muteChat" {}
   "wakuext_unmuteChat" {}
   "wakuext_contacts" {}
   "wakuext_prepareContent" {}
   "wakuext_blockContact" {}
   "wakuext_updateMailservers" {}
   "wakuext_sendEmojiReaction" {}
   "wakuext_sendEmojiReactionRetraction" {}
   "wakuext_emojiReactionsByChatID" {}
   ;;TODO not used anywhere?
   "wakuext_deleteChat" {}
   "wakuext_saveContact" {}
   "wakuext_verifyENSNames" {}
   "wakuext_requestAddressForTransaction" {}
   "wakuext_requestTransaction" {}
   "wakuext_acceptRequestAddressForTransaction" {}
   "wakuext_declineRequestAddressForTransaction" {}
   "wakuext_declineRequestTransaction" {}
   "wakuext_sendTransaction" {}
   "wakuext_acceptRequestTransaction" {}
   "wakuext_signMessageWithChatKey" {}
   "wakuext_sendGroupChatInvitationRequest" {}
   "wakuext_sendGroupChatInvitationRejection" {}
   "wakuext_getGroupChatInvitations" {}
   "wakuext_registerForPushNotifications" {}
   "wakuext_unregisterFromPushNotifications" {}
   "wakuext_enablePushNotificationsFromContactsOnly" {}
   "wakuext_disablePushNotificationsFromContactsOnly" {}
   "wakuext_startPushNotificationsServer" {}
   "wakuext_stopPushNotificationsServer" {}
   "wakuext_disableSendingNotifications" {}
   "wakuext_enableSendingNotifications" {}
   "wakuext_addPushNotificationsServer" {}
   "wakuext_getPushNotificationsServers" {}
   "wakuext_enablePushNotificationsBlockMentions" {}
   "wakuext_disablePushNotificationsBlockMentions" {}
   "status_chats" {}
   "wallet_getTransfers" {}
   "wallet_getTokensBalances" {}
   "wallet_getBlocksByAddress" {}
   "wallet_getTransfersFromBlock" {}
   "wallet_getTransfersByAddress" {}
   "wallet_getCustomTokens" {}
   "wallet_addCustomToken" {}
   "wallet_addFavourite" {}
   "wallet_getFavourites" {}
   "wallet_deleteCustomToken" {}
   "browsers_getBrowsers" {}
   "browsers_addBrowser" {}
   "browsers_deleteBrowser" {}
   "mailservers_getMailserverRequestGaps" {}
   "mailservers_addMailserverRequestGaps" {}
   "mailservers_deleteMailserverRequestGaps" {}
   "mailservers_deleteMailserverRequestGapsByChatID" {}
   "permissions_addDappPermissions" {}
   "permissions_getDappPermissions" {}
   "permissions_deleteDappPermissions" {}
   "settings_saveSetting" {}
   "settings_getSettings" {}
   "accounts_getAccounts" {}
   "accounts_saveAccounts" {}
   "accounts_deleteAccount" {}
   "mailservers_ping" {}
   "mailservers_addMailserver" {}
   "mailservers_getMailservers" {}
   "mailservers_deleteMailserver" {}
   "mailservers_addMailserverTopic" {}
   "mailservers_getMailserverTopics" {}
   "mailservers_deleteMailserverTopic" {}
   "mailservers_addChatRequestRange" {}
   "mailservers_getChatRequestRanges" {}
   "mailservers_deleteChatRequestRange" {}})

(defn on-error-retry
  [call-method {:keys [method number-of-retries delay on-error] :as arg}]
  (if (pos? number-of-retries)
    (fn [error]
      (let [updated-delay (if delay
                            (min 2000 (* 2 delay))
                            50)]
        (log/debug "[on-error-retry]" method
                   "number-of-retries" number-of-retries
                   "delay" delay
                   "error" error)
        (utils/set-timeout #(call-method (-> arg
                                             (update :number-of-retries dec)
                                             (assoc :delay updated-delay)))
                           updated-delay)))
    on-error))

(defn call-ext-method [waku-enabled? method]
  (if waku-enabled?
    (str "wakuext_" method)
    (str "shhext_" method)))

(defn call
  [{:keys [method params on-success on-error] :as arg}]
  (if-let [method-options (json-rpc-api method)]
    (let [params (or params [])
          {:keys [id on-result subscription?]
           :or {on-result identity
                id 1}} method-options
          on-error (or
                    on-error
                    (on-error-retry call arg)
                    #(log/warn :json-rpc/error method :error % :params params))]
      (if (nil? method)
        (do
          (log/error :json-rpc/method-not-found method)
          (on-error :json-rpc/method-not-found))
        (status/call-private-rpc
         (types/clj->json {:jsonrpc "2.0"
                           :id      id
                           :method  (if subscription?
                                      "eth_subscribeSignal"
                                      method)
                           :params  (if subscription?
                                      [method params]
                                      params)})
         (fn [response]
           (if (string/blank? response)
             (on-error {:message "Blank response"})
             (let [{:keys [error result]} (types/json->clj response)]
               (if error
                 (on-error error)
                 (if subscription?
                   (re-frame/dispatch
                    [:ethereum.callback/subscription-success
                     result on-success])
                   (on-success (on-result result))))))))))

    (log/warn "method" method "not found" arg)))

(defn eth-call
  [{:keys [contract method params outputs on-success block]
    :or {block "latest"
         params []}
    :as arg}]
  (call {:method "eth_call"
         :params [{:to contract
                   :data (abi-spec/encode method params)}
                  (if (int? block)
                    (abi-spec/number-to-hex block)
                    block)]
         :on-success
         (if outputs
           #(on-success (abi-spec/decode % outputs))
           on-success)
         :on-error
         (on-error-retry eth-call arg)}))

;; effects
(re-frame/reg-fx
 ::call
 (fn [params]
   (doseq [param params]
     (call param))))

(re-frame/reg-fx
 ::eth-call
 (fn [params]
   (doseq [param params]
     (eth-call param))))
