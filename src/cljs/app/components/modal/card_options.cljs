(ns app.components.modal.card-options
  (:require [app.i18n]
            [app.logic.game :as logic.game]
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

(defn ^:private stored-card-action [option-1? option-2?]
  (cond
    option-1? (reset! confirm-state-action logic.game/play-action)
    option-2? (reset! confirm-state-action logic.game/discard-action)
    :else (reset! confirm-state-action nil)))

(s/defn confirm-action
  [game :- Game
   card :- Card
   set-game-state]
  (let [function @confirm-state-action]
    (when function
      (function game card set-game-state))))

(defnc card-options-component [{:keys [game set-continue?]}]
  (let [[checked? set-checked?]  (hooks/use-state {:first? false :second? false})
        [disable-play set-disable-play] (hooks/use-state false)
        disabled-play-fn  #(if (> 5 (-> game :table count)) (set-disable-play false) (set-disable-play true))
        content' (app.i18n/app-tr [:modal.card-options/select-action])
        play' (app.i18n/app-tr [:modal.card-options/play])
        discard' (app.i18n/app-tr [:modal.card-options/discard])]
    (hooks/use-effect :once (set-continue? false))
    (hooks/use-effect [(-> game :table count)] (disabled-play-fn))
    (hooks/use-memo [(:first? checked?)
                     (:second? checked?)]
                    (stored-card-action (:first? checked?) (:second? checked?)))
    (<>
     (d/p content')
     (d/span {:class "modal-options"}
             (d/button {:on-click #(set-option set-continue? set-checked? true false)
                        :disabled disable-play
                        :class "modal-option"
                        :style {:display "flex"}}
                       (d/input {:class "draw-option"
                                 :checked (:first? checked?)
                                 :read-only true
                                 :disabled disable-play
                                 :type "radio"})
                       (d/label play'))
             (d/button {:on-click #(set-option set-continue? set-checked? false true)
                        :class "modal-option"
                        :style {:display "flex"}}
                       (d/input {:class "discard-option"
                                 :checked (:second? checked?)
                                 :read-only true
                                 :type "radio"})
                       (d/label discard'))))))
