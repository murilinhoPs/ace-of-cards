(ns app.components.player-actions
  (:require [app.logic.game :as logic.game]
            [helix.core :refer [defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

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
                                          :content #(d/p "Deseja usar essas cartas? Essa ação não pode ser desfeita!")}))}
                      "Resolve Cards")
            (d/button {:class "trap-card-btn"
                       :on-click nil}
                      "Trap Card"))))
