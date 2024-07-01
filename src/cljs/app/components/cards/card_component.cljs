(ns app.components.cards.card-component
  (:require [app.adapters.card-suit :refer [card-suits->icon]]
            [app.utils :refer [asset]]
            [helix.core :refer [<> defnc]]
            [helix.dom :as d]))

(defnc card-component
  [{:keys [rank suit on-click]}]
  (d/button  {:on-click (if (nil? on-click) #() #(on-click))
              :class "hand-card-button"
              :style {:border "4px solid var(--lighter-color)"
                      :color "var(--text-color)"
                      :border-radius "16px"
                      :padding "8px"
                      :justify-content "center"
                      :align-items "center"
                      :display "flex"
                      :flex-direction "column"
                      :width "8.6rem"
                      :height "12rem"
                      :position "relative"}}
             (d/p {:style {:font-size "2.4rem"}} rank)
             (if (= :joker suit)
               (<> (d/img {:src (asset "joker.png")
                           :width "28px"
                           :style {:filter "invert(100%)"
                                   :position "absolute"
                                   :top "12px"
                                   :left "12px"}})
                   (d/img {:src (asset "joker.png")
                           :width "28px"
                           :style {:filter "invert(100%)"
                                   :position "absolute"
                                   :bottom "12px"
                                   :right "12px"}}))
               (<> (d/i {:class (card-suits->icon suit)
                         :style {:position "absolute"
                                 :top "12px"
                                 :right "12px"
                                 :font-size "28px"}})
                   (d/i {:class (card-suits->icon suit)
                         :style {:position "absolute"
                                 :bottom "12px"
                                 :left "12px"
                                 :font-size "28px"}})))))