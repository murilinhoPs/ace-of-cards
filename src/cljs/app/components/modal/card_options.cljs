(ns app.components.modal.card-options
  (:require [clj.ace-of-cards.actions :refer [discard-cards]]
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
  (let [new-game (discard-cards game :hand card)]
    (print  (str "game: " new-game))
    (set-game-state new-game)))

(defn ^:private stored-card-action [option-1? option-2?]
  (cond ;;TODO: create function to draw a card
    option-1? (reset! confirm-state-action #(print "draw"))
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
  (let [[checked? set-checked?]  (hooks/use-state {:first? false :second? false})]
    (hooks/use-effect :once (set-continue? false))
    (hooks/use-memo [(:first? checked?)
                     (:second? checked?)]
                    (stored-card-action (:first? checked?) (:second? checked?)))
    (<>
     (d/p "Select what you want to do with this card:")
     (d/span {:class "modal-options"}
             (d/article {:on-click #(set-option set-continue? set-checked? true false)
                         :class "modal-option"
                         :style {:display "flex"}}
                        (d/input {:class "draw-option"
                                  :checked (:first? checked?)
                                  :read-only true
                                  :type "radio"})
                        (d/label "Play"))
             (d/article {:on-click #(set-option set-continue? set-checked? false true)
                         :class "modal-option"
                         :style {:display "flex"}}
                        (d/input {:class "discard-option"
                                  :checked (:second? checked?)
                                  :read-only true
                                  :type "radio"})
                        (d/label "Discard"))))))
