(ns quo.previews.main
  (:require [oops.core :refer [oget ocall]]
            [quo.previews.header :as header]
            [quo.previews.text :as text]
            [quo.previews.text-input :as text-input]
            [quo.previews.tooltip :as tooltip]
            [quo.react-native :as rn]
            [quo.react :as react]
            [quo.core :as quo]
            [reagent.core :as reagent]
            [quo.design-system.colors :as colors]
            [quo.theme :as theme]
            ["@react-navigation/native" :refer (NavigationContainer CommonActions)]
            ["@react-navigation/stack" :refer (createStackNavigator)]))


(defonce navigator-ref (react/create-ref))

(defn navigate-to [route params]
  (when-let [navigator (react/current-ref navigator-ref)]
    (ocall navigator "dispatch"
           (ocall CommonActions "navigate"
                  #js {:name   (name route)
                       :params (clj->js params)}))))

(def screens [{:name      :texts
               :insets    {:top false}
               :component text/preview-text}
              {:name      :tooltip
               :insets    {:top false}
               :component tooltip/preview-tooltip}
              {:name      :text-input
               :insets    {:top false}
               :component text-input/preview-text}
              {:name      :headers
               :insets    {:top false}
               :component header/preview-header}])

(defn theme-switcher []
  [rn/view {:style {:flex-direction   :row
                    :margin-vertical  8
                    :border-radius    4
                    :background-color (:ui-01 @colors/theme)
                    :border-width     1
                    :border-color     (:ui-02 @colors/theme)}}
   [rn/touchable-opacity {:style    {:padding         8
                                     :flex            1
                                     :justify-content :center
                                     :align-items     :center}
                          :on-press #(theme/set-theme :light)}
    [quo/text "Set light theme"]]
   [rn/view {:width            1
             :margin-vertical  4
             :background-color (:ui-02 @colors/theme)}]
   [rn/touchable-opacity {:style    {:padding         8
                                     :flex            1
                                     :justify-content :center
                                     :align-items     :center}
                          :on-press #(theme/set-theme :dark)}
    [quo/text "Set dark theme"]]])

(defn main-screen []
  [rn/scroll-view {:flex               1
                   :padding-vertical   8
                   :padding-horizontal 16
                   :background-color   (:ui-background @colors/theme)}
   [theme-switcher]
   [rn/view
    (for [{:keys [name]} screens]
      [rn/touchable-opacity {:on-press #(navigate-to name nil)}
       [rn/view {:style {:padding-vertical 8}}
        [quo/text (str "Preview " name)]]])]])

(defonce navigation-state (atom nil))
(defn- persist-state! [state-obj]
  (js/Promise.
   (fn [resolve _]
     (reset! navigation-state state-obj)
     (resolve true))))

(defn preview-screens []
  (let [stack-obj (createStackNavigator)
        stack     (oget stack-obj "Navigator")
        screen    (oget stack-obj "Screen")]
    [:> NavigationContainer
     {:ref             navigator-ref
      :initial-state   @navigation-state
      :on-state-change persist-state!}
     (into [:> stack {}
        (map (fn [option] [:> screen (update option :component #(fn [] (reagent/as-element %)))])
             (into [{:name :main :component main-screen}] screens))])]))

(defn init []
  (.registerComponent ^js rn/app-registry "StatusQuo" #(reagent/reactify-component preview-screens)))
