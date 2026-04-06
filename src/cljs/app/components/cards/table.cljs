(ns app.components.cards.table
  (:require [app.components.cards.card-component :refer [card-component]]
            [app.i18n]
            [helix.core :refer [$]]
            [helix.dom :as d]))

(defn table-cards [table & {:keys [card-click table-ref drop-active? on-card-pointer-down
                                   dragging-card-id hover-zone holding-card-id]}]
  (let [table' (-> (app.i18n/app-tr [:cards/table]) (str " - Magic Cards"))]
    (d/article {:class (str "table"
                             (when drop-active? " drop-zone-active")
                             (when (= hover-zone :table) " drop-zone-hover"))
                :ref table-ref
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
                                  :key (:id card)
                                  :data-card-id (:id card)}
                                 ($ card-component {:rank (:rank card)
                                                    :suit (:suit card)
                                                    :on-click #(card-click card)
                                                    :on-pointer-down (when on-card-pointer-down #(on-card-pointer-down card %))
                                                    :dragging? (= (:id card) dragging-card-id)
                                                    :holding? (= (:id card) holding-card-id)}))))))))
