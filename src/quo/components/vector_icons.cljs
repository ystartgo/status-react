(ns quo.components.vector-icons
  (:require [quo.react-native :as rn]
            [quo.design-system.colors :as colors]))

;; FIXME(Ferossgp): keywords are for backweard compatibility
(defn- match-color [color]
  (cond
    (keyword? color)
    (case color
      :dark   (:icon-01 @colors/theme)
      :gray   (:icon-02 @colors/theme)
      :blue   (:interactive-01 @colors/theme)
      :active (:interactive-01 @colors/theme)
      :white  (:icon-04 @colors/theme)
      :red    (:negative-01 @colors/theme)
      :none   nil
      (:icon-01 @colors/theme))
    (string? color)
    color
    :else
    (:icon-01 @colors/theme)))

(defn icon-source [name]
  {:uri (keyword (clojure.core/name name))})

(defn icon
  ([name] (icon name nil))
  ([name {:keys [color resize-mode container-style
                 accessibility-label width height]
          :or   {accessibility-label :icon}}]
   ^{:key name}
   [rn/view
    {:style               (or
                           container-style
                           {:width  (or width 24)
                            :height (or height 24)})
     :accessibility-label accessibility-label}
    [rn/image {:style (cond-> {:width  (or width 24)
                               :height (or height 24)}

                        resize-mode
                        (assoc :resize-mode resize-mode)

                        :always
                        (assoc :tint-color (match-color color)))
               :source (icon-source name)}]]))

(defn tiny-icon
  ([name] (tiny-icon name {}))
  ([name options]
   (icon name (merge {:width 16 :height 16}
                     options))))
