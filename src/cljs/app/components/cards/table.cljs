(ns app.components.cards.table 
  (:require [app.components.cards.card-component :refer [card-component]]
            [helix.core :refer [$]]
            [helix.dom :as d]))

(defn table-cards [table & {:keys [card-click]}]
  (d/article {:class "table"}
             (d/h3 "Table")
             (when (< 0 (count table))
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"
                               :align-self "center"}}
                      (for [card table]
                        (d/div {:key (:id card)
                                :style {:padding "8px 0px"
                                        :display "flex"
                                        :column-gap "16px"}}
                               ($ card-component {:rank (:rank card)
                                                  :suit (:suit card)})))))))
