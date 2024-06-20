(ns app.components.decks.deck-component 
  (:require [app.logic.game :refer [draw-card]]
            [helix.core :refer [defnc]]
            [helix.dom :as d]))

(defnc deck-component [{:keys [count game-state set-game-state]}]
  (d/article {:class "deck"
              :style {:position "relative"}}
             (d/p {:style {:position "absolute"
                           :top "-20px"
                           :left "-12px"
                           :font-size "36px"}}
                  (str count))
             (d/div {:style {:border "4px solid var(--secondary-color)"
                             :background-color "var(--main-bg-color)"
                             :border-radius "16px"
                             :padding "8px"
                             :justify-content "center"
                             :align-items "center"
                             :display "flex"
                             :width "7rem"
                             :height "9rem"
                             :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color), 
                                 -12px 12px 0 0 var(--secondary-color), 
                                 -18px 20px 0 0 var(--main-bg-color), 
                                 -22px 24px 0 0 var(--secondary-color)"}}
                    (d/i {:class "icon-layers" :style {:font-size "54px"}}))
             (d/button  {:id "start-button"
                         :on-click #(draw-card game-state set-game-state)
                         :style {:margin "2rem 0 0"
                                 :background-color "var(--secondary-color)"
                                 :color "var(--text-color)"
                                 :border "none"
                                 :border-radius "12px"
                                 :padding "8px"
                                 :font-weight "bold"
                                 :max-height "36px"
                                 :min-width "120px"}}
                        "Draw")))
