(ns app.components.decks.discard-pile-component
  (:require ["lucide-react" :refer [Trash2]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc discard-pile-component [{:keys [count]}]
  (d/article {:id "deck"
              :class "discard"
              :style {:position "relative"}}
             (d/p {:id "deck-count"}
                  count)
             (d/div {:id "card-deck"
                     :style {:border "4px solid var(--main-color)"
                             :background-color "var(--main-bg-color)"
                             :border-radius "16px"
                             :padding "8px "
                             :justify-content "center"
                             :align-items "center"
                             :display "flex"
                             :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color), 
                               -12px 12px 0 0 var(--main-color), 
                               -18px 20px 0 0 var(--main-bg-color), 
                               -22px 24px 0 0 var(--main-color)"}}
                    ($ Trash2 {:class "deck-icon"}))))
