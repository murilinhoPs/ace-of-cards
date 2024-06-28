(ns app.components.card-list-component
  (:require [app.adapters.card-suit :refer [card-suits->icon]]
            [app.utils :refer [asset]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc joker-card []
  (d/li {:id "joker-card" :class "card-item"}
        (d/input {:type "radio"
                  :name "category"
                  :value "joker"
                  :data-label "Joker"})
        (d/img {:src (asset "joker.png")
                :style {:filter "invert(100%)"
                        :width "16px"}})
        (d/span {:class "label"} "Joker")

        (d/i {:class "icon-check"})))

(defnc default-card [{:keys [rank suit]}]
  (d/li {:class "card-item"}
        (d/input {:type "radio"
                  :name "category"
                  :value (str rank "-" suit)
                  :data-label (str rank "-" suit)})
        (d/i {:class (card-suits->icon suit)})
        (d/span {:class "label"} rank)

        (d/i {:class "icon-check"})))

(defnc card-list-component [{:keys [coll]}]
  (when (> (count coll) 0)
    (d/div {:class "select"}
           (d/div {:id "category-select"}
                  (d/input {:type "checkbox"
                            :id "options-button"})
                  (d/div {:id "select-button"}
                         (d/p {:id "selected-value"}
                              "Cartas")
                         (d/div {:id "chevrons"}
                                (d/i {:class "icon-chevron-down"})
                                (d/i {:class "icon-chevron-up"}))))
           (d/ul {:id "cards-list"}
                 (for [card coll]
                   (if (= :joker (:suit card))
                     ($ joker-card {:key (:id card)})
                     ($ default-card {:key (:id card)
                                      :rank (:rank card)
                                      :suit (:suit card)})))))))
