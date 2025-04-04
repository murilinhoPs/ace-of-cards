(ns app.components.cards.hand 
  (:require [app.components.cards.card-component :refer [card-component]]
            [app.i18n]
            [helix.core :refer [$]]
            [helix.dom :as d]))

(defn hand-cards [hand & {:keys [card-click]}]
  (d/article {:class "hand"}
             (d/h3 (app.i18n/app-tr [:cards/hand]))
             (when (< 0 (count hand))
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"
                               :padding "8px 0px 0px"}}
                      (for [card hand]
                        (d/div {:key (:id card)}
                               ($ card-component {:rank (:rank card)
                                                  :suit (:suit card)
                                                  :on-click #(card-click card)})))))))
