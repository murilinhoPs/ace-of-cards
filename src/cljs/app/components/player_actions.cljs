(ns app.components.player-actions
  (:require [app.logic.game :as logic.game]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc ^:private trap-card-content []
  (d/div {:style {:display "flex"
                  :flex-direction "column"
                  :gap "1.2rem"}}
         (d/p "Usar essa habilidade? Essa ação não pode ser desfeita!")
         (d/small {:style {:color "#b2b9af"}}
          "Essa habilidade vai colocar a última carta do deck em primeiro lugar na pilha de descarte. 
            Se essa carta for do naipe e do valor escolhido, ou um coringa, você pode usar um feitiço")))

(defnc player-actions-footer  [{:keys [game-state set-game-state set-modal-state]}]
  (let [[disable-resolve set-disable-resolve] (hooks/use-state false)
        disable-resolve? (<= (-> game-state :table count) 1)]
    (hooks/use-effect [(-> game-state :table count)]
                      (set-disable-resolve disable-resolve?))
    (d/footer {:id "footer-actions"}
              (d/h3 {:class "footer-title"}
                    "Player Actions:")
              (d/button {:class "resolve-cards-btn"
                         :disabled disable-resolve
                         :on-click (fn [] (set-modal-state
                                           {:show? true?
                                            :confirm-click (partial logic.game/resolve-cards-action game-state (:table game-state) set-game-state)
                                            :content #(d/p "Usar essas cartas? Essa ação não pode ser desfeita!")}))}
                        "Resolve Cards")
              (d/button {:class "trap-card-btn"
                         :on-click (fn [] (set-modal-state
                                           {:show? true?
                                            :confirm-click (partial logic.game/use-trap-card-action game-state set-game-state)
                                            :content #($ trap-card-content)}))}
                        "Trap Card"))))
