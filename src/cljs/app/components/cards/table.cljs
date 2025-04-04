(ns app.components.cards.table
  (:require [app.components.cards.card-component :refer [card-component]]
            [app.i18n]
            [helix.core :refer [$]]
            [helix.dom :as d]))

(defn table-cards [table & {:keys [card-click]}]
  (let [table' (-> (app.i18n/app-tr [:cards/table]) (str " - Magic Cards"))]
    (d/article {:class "table"
                :style {:min-height "320px"}}
               (d/h3 table')
               (when (< 0 (count table))
                 (d/div {:style {:display "flex"
                                 :flex-direction "row"
                                 :flex-wrap "wrap"
                                 :gap "16px"
                                 :align-self "center"
                                 :padding "8px 0px"
                                 :margin-top "12px"}}
                        (for [card table]
                          (d/div {:id "table-card"
                                  :key (:id card)}
                                 ($ card-component {:rank (:rank card)
                                                    :suit (:suit card)
                                                    :on-click #(card-click card)}))))))))
