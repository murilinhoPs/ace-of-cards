(ns app.components.modal.card-options
  (:require [clj.ace-of-cards.actions :refer [discard-cards]]
            [clj.ace-of-cards.card :refer [Card]]
            [clj.ace-of-cards.game :refer [Game]]
            [helix.core :refer [defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [schema.core :as s]))

(defn ^:private set-option
  [continue? set-state first second]
  (set-state assoc :first? first)
  (set-state assoc :second? second)
  (continue? true))

(def ^:private confirm-state-action (atom nil)) ;; *atom armazena um estado da variavel, tipo elevate state

(s/defn ^:private discard-action ;;!on-going
  [game :- Game
   card :- Card]
  (discard-cards game :hand card))

(defn ^:private card-action [option-1? option-2?]
  (cond ;;TODO: create function to draw and discard a card
    option-1? (reset! confirm-state-action #(print "draw"))
    option-2? (reset! confirm-state-action #(print "discard"))
    :else (reset! confirm-state-action  #(print "nothing"))))

(s/defn confirm-action [game :- game/Game] 
  (@confirm-state-action game))

(defnc card-options-component [{:keys [set-continue?]}]
  (let [[checked? set-checked?]  (hooks/use-state {:first? false :second? false})]
    (hooks/use-effect :once (set-continue? false))
    (hooks/use-effect
     [(:first? checked?) (:second? checked?)]
     (print "change option")
     (card-action (:first? checked?) (:second? checked?)))
    (d/span {:class "modal-options"}
            (d/article {:on-click #(set-option set-continue? set-checked? true false)
                        :class "modal-option"
                        :style {:display "flex"}}
                       (d/input {:class "draw-option"
                                 :checked (:first? checked?)
                                 :read-only true
                                 :type "radio"})
                       (d/label "Draw"))
            (d/article {:on-click #(set-option set-continue? set-checked? false true)
                        :class "modal-option"
                        :style {:display "flex"}}
                       (d/input {:class "discard-option"
                                 :checked (:second? checked?)
                                 :read-only true
                                 :type "radio"})
                       (d/label "Discard")))))
