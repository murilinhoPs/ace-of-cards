(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [app.components.game-component :refer [game-component]]
            [app.components.modal.modal-base :refer [modal-base]]
            [app.logic.game :as logic.game]
            [clj.ace-of-cards.core :refer [new-game]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc app []
  (let [[started? set-started?] (hooks/use-state false)
        [game-state set-game-state] (hooks/use-state {:deck [], :hand [], :discard-pile [], :table []})
        [modal-state set-modal-state] (hooks/use-state {:show? false, :content nil, :confirm nil})
        start (fn [] (set-started? true) (logic.game/update-game-state set-game-state (new-game true)))
        restart #(logic.game/restart-game set-modal-state start)]
    (d/div
     (d/header {:class "header" :style {:display "flex"
                                        :justify-content "space-between"
                                        :align-items "center"
                                        :margin "12px 12px 48px"}}
               (d/h1 "Ace of Cards - Fabula Ultima")
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"
                               :align-self "center"}}
                      (when started? (d/button  {:id "header-button"
                                                 :on-click #(logic.game/shuffle-deck game-state set-game-state)
                                                 :style {:background-color "var(--secondary-color)"
                                                         :color "var(--text-color)"
                                                         :border "none"
                                                         :border-radius "12px"
                                                         :padding "8px"
                                                         :font-weight "bold"
                                                         :max-height "36px"
                                                         :min-width "120px"}}
                                                "Shuffle Deck"))
                      (d/button  {:id "header-button"
                                  :on-click (if started? #(restart) #(start))
                                  :style {:background-color  (if started? "var(--main-color)" "var(--secondary-color)")
                                          :color "var(--text-color)"
                                          :border "none"
                                          :border-radius "12px"
                                          :padding "8px"
                                          :font-weight "bold"
                                          :max-height "36px"
                                          :min-width "120px"}}
                                 (if started? "Reset Game" "New Game"))))
     (when started? ($ game-component {:game-state game-state
                                       :set-game-state set-game-state
                                       :set-modal-state set-modal-state}))
     (when (:show? modal-state) ($ modal-base {:set-show-modal set-modal-state
                                               :content  (:content modal-state)
                                               :confirm-click (:confirm-click modal-state)})))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
