(ns app.components.card-list-component
  (:require ["lucide-react" :refer [Check ChevronUp ChevronDown]]
            ["lucide-react/dynamic" :refer [DynamicIcon]]
            [app.adapters.card-suit :refer [card-suits->icon]]
            [app.i18n]
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

        ($ Check {:class "icon-check" :size "16px"})))

(defnc default-card [{:keys [rank suit]}]
  (d/li {:class "card-item"}
        (d/input {:type "radio"
                  :name "category"
                  :value (str rank "-" suit)
                  :data-label (str rank "-" suit)})
        ($ DynamicIcon {:name (card-suits->icon suit) :size "16px"})
        (d/span {:class "label"} rank)

        ($ Check {:class "icon-check" :size "16px"})))

(defnc card-list-component [{:keys [coll]}]
  (when (> (count coll) 0)
    (d/div {:id "card-list"
            :class "select"}
           (d/div {:id "category-select"}
                  (d/input {:type "checkbox"
                            :id "options-button"})
                  (d/div {:id "select-button"}
                         (d/p {:id "selected-value"}
                              (app.i18n/app-tr [:card-list/cards]))
                         (d/div {:id "chevrons"}
                                ($ ChevronUp {:class "icon-chevron-down"})
                                ($ ChevronDown {:class "icon-chevron-up"}))))
           (d/ul {:id "cards-list"}
                 (for [card coll]
                   (if (= :joker (:suit card))
                     ($ joker-card {:key (:id card)})
                     ($ default-card {:key (:id card)
                                      :rank (:rank card)
                                      :suit (:suit card)})))))))
