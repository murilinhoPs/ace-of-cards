(ns app.components.decks.deck-component
  (:require ["lucide-react" :refer [Layers]]
            [app.i18n]
            [app.logic.game :refer [draw-card]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc deck-component [{:keys [count game-state set-game-state]}]
  (d/article {:id "deck"
              :class "buy"
              :style {:position "relative"
                      :display "flex"
                      :flex-direction "column"
                      :align-items "end"}}
             (d/p {:id "deck-count"}
                  (str count))
             (d/div {:id "card-deck"
                     :style {:border "4px solid var(--secondary-color)"
                             :background-color "var(--main-bg-color)"
                             :border-radius "16px"
                             :justify-content "center"
                             :align-items "center"
                             :display "flex"
                             :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color),
                                 -12px 12px 0 0 var(--secondary-color),
                                 -18px 20px 0 0 var(--main-bg-color),
                                 -22px 24px 0 0 var(--secondary-color)"}}
                    ($ Layers {:class "deck-icon"}))
             (d/button  {:id "start-button"
                         :on-click #(draw-card game-state set-game-state)
                         :style {:margin "2rem 0 0" 
                                 :justify-content "center"
                                 :background-color "var(--secondary-color)"
                                 :color "var(--text-color)"
                                 :border "none"
                                 :border-radius "12px"
                                 :padding "8px 12px"
                                 :font-size ".9rem"
                                 :font-weight "bold"
                                 :max-height "36px"
                                 :min-width "80px"}}
                        (app.i18n/app-tr [:decks/draw]))))
