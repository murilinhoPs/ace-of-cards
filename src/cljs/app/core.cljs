(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [app.components.game-component :refer [game-component]]
            [app.components.header-component :refer [header-component]]
            [app.components.modal.modal-base :refer [modal-base]]
            [app.components.player-actions :refer [player-actions-footer]]
            [app.logic.game :as logic.game]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc app []
  (let [[started? set-started?] (hooks/use-state false)
        [game-state set-game-state] (hooks/use-state {:deck [], :hand [], :discard-pile [], :table []})
        [modal-state set-modal-state] (hooks/use-state {:show? false, :content nil, :confirm nil})
        start-game #(logic.game/start-game set-started? set-game-state)
        restart-game #(logic.game/restart-game set-modal-state start-game)
        shuffle-game #(logic.game/shuffle-deck game-state set-game-state)]
    (d/div {:id "content"}
           ($ header-component {:started? started?
                                :start-game start-game
                                :restart-game restart-game
                                :shuffle-deck shuffle-game})
           (when started? (<> ($ game-component {:game-state game-state
                                                    :set-game-state set-game-state
                                                    :set-modal-state set-modal-state})
                                 ($ player-actions-footer {:game-state game-state
                                                           :set-game-state set-game-state
                                                           :set-modal-state set-modal-state})))
           (when (:show? modal-state) ($ modal-base {:game game-state
                                                     :set-show-modal set-modal-state
                                                     :content  (:content modal-state)
                                                     :confirm-click (:confirm-click modal-state)})))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (print (.-language js/navigator))
  (.render root ($ app)))
