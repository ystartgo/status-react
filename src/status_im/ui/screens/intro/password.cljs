(ns status-im.ui.screens.intro.password
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [status-im.ui.components.topbar :as topbar]
            [status-im.ui.components.toolbar :as toolbar]
            [status-im.i18n :as i18n]
            [status-im.constants :as const]
            [status-im.utils.security :as security]
            [quo.react-native :as rn]
            [quo.react :as react]
            [quo.core :as quo]))

(defn validate-password [password]
  (>= (count password) const/min-password-length))

(defn confirm-password [password confirm]
  (= password confirm))

(defn screen []
  (let [password    (reagent/atom nil)
        confirm     (reagent/atom nil)
        confirm-ref (react/create-ref)]
    (fn []
      (let [{:keys [forward-action processing?]}
            @(re-frame/subscribe [:intro-wizard/create-code])
            valid-password (validate-password @password)
            valid-form     (confirm-password @password @confirm)
            on-submit      (fn []
                             (when (and (not processing?) valid-form)
                               (re-frame/dispatch [forward-action {:key-code @password}])))]
        [rn/keyboard-avoiding-view {:flex 1}
         [topbar/topbar
          {:navigation
           {:icon                :main-icons/back
            :accessibility-label :back-button
            :handler             #(re-frame/dispatch [:intro-wizard/navigate-back])}}]
         [rn/view {:style {:flex               1
                           :justify-content    :space-between
                           :padding-vertical   16
                           :padding-horizontal 16}}

          [rn/view
           [quo/text {:weight :bold
                      :align  :center
                      :size   :x-large}
            (i18n/label :intro-wizard-title-alt4)]]
          [rn/view
           [rn/view {:style {:padding 16}}
            [quo/text-input {:secure-text-entry   true
                             :auto-capitalize     :none
                             :auto-focus          true
                             :show-cancel         false
                             :accessibility-label :password-input
                             :placeholder         "Password..."
                             :on-change-text      #(reset! password (security/mask-data %))
                             :return-key-type     :next
                             :on-submit-editing   #(when valid-password
                                                     (some-> confirm-ref
                                                             react/current-ref
                                                             .focus))}]]
           [rn/view {:style {:padding 16
                             :opacity (if-not valid-password 0.33 1)}}
            [quo/text-input {:secure-text-entry   true
                             :ref                 confirm-ref
                             :auto-capitalize     :none
                             :show-cancel         false
                             :accessibility-label :password-input
                             :editable            valid-password
                             :placeholder         "Confirm your password..."
                             :return-key-type     :go
                             :blur-on-submit      true
                             :on-submit-editing   on-submit
                             :on-change-text      #(reset! confirm (security/mask-data %))}]]]
          (when processing?
            [rn/view {:align-items      :center
                      :padding-vertical 8}
             [rn/activity-indicator {:size      :large
                                     :animating true}]
             [rn/view {:padding-vertical 8}
              [quo/text {:color :secondary}
               (i18n/label :t/processing)]]])
          [rn/view
           [quo/text {:color :secondary
                      :align :center
                      :size  :small}
            (i18n/label :t/password-description)]]]
         [toolbar/toolbar
          (merge {:show-border? true}
                 {:right
                  [quo/button
                   {:on-press            on-submit
                    :accessibility-label :onboarding-next-button
                    :disabled            (or (not valid-form)
                                             processing?)
                    :type                :secondary
                    :after               :main-icons/next}
                   (i18n/label :t/next)]})]]))))
