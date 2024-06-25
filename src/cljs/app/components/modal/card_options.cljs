(ns app.components.modal.card-options
  (:require [app.i18n]
            [clj.ace-of-cards.actions :refer [discard-cards play-card]]
            [clj.ace-of-cards.card :refer [Card]]
            [clj.ace-of-cards.game :refer [Game]]
            [helix.core :refer [<> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [schema.core :as s]))

(defn ^:private set-option
  [continue? set-state first second]
  (set-state assoc :first? first)
  (set-state assoc :second? second)
  (continue? true))

(def ^:private confirm-state-action (atom nil)) ;; *atom armazena um estado da variavel, tipo elevate state

(s/defn ^:private discard-action
  [game :- Game
   card :- Card
   set-game-state]
  (-> (discard-cards game :hand card)
      set-game-state))

(s/defn ^:private play-action
  [game :- Game
   card :- Card
   set-game-state]
  (-> (play-card game card)
      set-game-state))

(defn ^:private stored-card-action [option-1? option-2?]
  (cond
    option-1? (reset! confirm-state-action play-action)
    option-2? (reset! confirm-state-action discard-action)
    :else (reset! confirm-state-action nil)))

(s/defn confirm-action
  [game :- Game
   card :- Card
   set-game-state]
  (let [function @confirm-state-action]
    (when function
      (function game card set-game-state))))

(defnc card-options-component [{:keys [set-continue?]}]
  (let [[checked? set-checked?]  (hooks/use-state {:first? false :second? false})
        content' (app.i18n/app-tr [:modal.card-options/select-action])
        play' (app.i18n/app-tr [:modal.card-options/play])
        discard' (app.i18n/app-tr [:modal.card-options/discard])]
    (hooks/use-effect :once (set-continue? false))
    (hooks/use-memo [(:first? checked?)
                     (:second? checked?)]
                    (stored-card-action (:first? checked?) (:second? checked?)))
    (<>
     (d/p content')
     (d/span {:class "modal-options"}
             (d/article {:on-click #(set-option set-continue? set-checked? true false)
                         :class "modal-option"
                         :style {:display "flex"}}
                        (d/input {:class "draw-option"
                                  :checked (:first? checked?)
                                  :read-only true
                                  :type "radio"})
                        (d/label play'))
             (d/article {:on-click #(set-option set-continue? set-checked? false true)
                         :class "modal-option"
                         :style {:display "flex"}}
                        (d/input {:class "discard-option"
                                  :checked (:second? checked?)
                                  :read-only true
                                  :type "radio"})
                        (d/label discard'))))))
