(ns status-im.ui.screens.desktop.views
  (:require-macros [status-im.utils.views :as views])
  (:require [status-im.ui.screens.desktop.main.views :as main.views]
            [status-im.ui.screens.home.views :as home.views]
            [status-im.ui.components.react :as react]
            [status-im.ui.screens.intro.views :as intro.views]
            [status-im.ui.screens.group.views :refer [contact-toggle-list
                                                      new-group
                                                      add-participants-toggle-list]]
            [status-im.ui.screens.profile.group-chat.views :refer [group-chat-profile]]
            [status-im.ui.screens.multiaccounts.login.views :as login.views]
            [status-im.ui.screens.multiaccounts.recover.views :as recover.views]
            [status-im.ui.screens.multiaccounts.views :as multiaccounts.views]
            [status-im.utils.platform :as platform]
            [status-im.i18n :as i18n]
            [status-im.react-native.js-dependencies :as rn-dependencies]
            [taoensso.timbre :as log]
            [status-im.utils.utils :as utils]
            [status-im.ui.screens.routing.intro-login-stack :as intro-login-stack]))

(enable-console-print!)

(views/defview main []
  (views/letsubs [view-id [:view-id]
                  version [:get-app-version]
                  screen-params [:screen-params]
                  multiaccounts [:multiaccounts/multiaccounts]
                  loading [:multiaccounts/loading]]
    {:component-did-mount (fn []
                            (.getValue rn-dependencies/desktop-config "desktop-alpha-warning-shown-for-version"
                                       #(when-not (= %1 version)
                                          (.setValue rn-dependencies/desktop-config "desktop-alpha-warning-shown-for-version" version)
                                          (utils/show-popup nil (i18n/label :desktop-alpha-release-warning)))))}
    (let [view-id        (or view-id :intro-stack) ;; TODO some default value
          screen-params2 (or screen-params {:intro-stack {:screen :intro}})
          comp           ((intro-login-stack/intro-stack-desktop view-id screen-params2 loading multiaccounts))
          component      (case view-id
                      ;; :intro intro.views/intro
                      ;; :create-multiaccount-generate-key intro.views/wizard-generate-key
                      ;; :create-multiaccount-choose-key intro.views/wizard-choose-key
                      ;; :create-multiaccount-create-code intro.views/wizard-create-code
                      ;; :create-multiaccount-confirm-code intro.views/wizard-confirm-code
                      ;; :recover-multiaccount-enter-phrase intro.views/wizard-enter-phrase
                           :welcome home.views/welcome
                           :multiaccounts multiaccounts.views/multiaccounts
                           :new-group  new-group
                           :contact-toggle-list contact-toggle-list
                           :group-chat-profile group-chat-profile
                           :add-participants-toggle-list add-participants-toggle-list
                           :intro-stack login.views/login
                      ;; :chat-stack (intro-login-stack/chat-stack-desktop view-id screen-params)
                      ;; :wallet-stack (intro-login-stack/wallet-stack-desktop view-id screen-params)

                           (:desktop/new-one-to-one
                            :desktop/new-group-chat
                            :desktop/new-public-chat
                            :advanced-settings
                            :edit-mailserver
                            :bootnodes-settings
                            :edit-bootnode
                            :about-app
                            :help-center
                            :installations
                            :chat
                            :home
                            :qr-code
                            :chat-profile
                            :backup-recovery-phrase) main.views/main-views
                           :login login.views/login
                           react/view)]
      (log/debug ">>>>>>>>>>>>> comp " comp)
      [react/view {:style {:flex 1}}
       [(or comp component)]
       [main.views/popup-view]])))
