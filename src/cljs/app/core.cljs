(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [app.utils :refer [asset]]
            [clj.ace-of-cards.actions :as actions]
            [clj.ace-of-cards.card :as card]
            [clj.ace-of-cards.core :refer [new-game]]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [schema.core :as s]))

(defn ^:private _set-game-state
  [set-state data]
  (set-state assoc :hand (:hand data))
  (set-state assoc :deck (:deck data))
  (set-state assoc :discard-pile (:discard-pile data)))

(s/defn ^:private card-suits->icon :- s/Str
  [suit :- card/Suit]
  (cond
    (= :hearts suit) "icon-heart"
    (= :spades suit) "icon-spade"
    (= :clubs suit) "icon-club"
    (= :diamonds suit) "icon-diamond"))

(defnc card-component
  [{:keys [rank suit]}]
  (d/div {:style {:border "4px solid var(--lighter-color)"
                  :background-color "var(--main-bg-color)"
                  :border-radius "16px"
                  :padding "8px"
                  :justify-content "center"
                  :align-items "center"
                  :display "flex"
                  :flex-direction "column"
                  :width "10rem"
                  :height "14rem"
                  :position "relative"}}
         (d/text {:style {:font-size "3rem"}} rank)
         (if (= :joker suit)
           (<> (d/img {:src (asset "joker.png")
                       :width "36px"
                       :style {:filter "invert(100%)"
                               :position "absolute"
                               :top "12px"
                               :left "12px"}})
               (d/img {:src (asset "joker.png")
                       :width "36px"
                       :style {:filter "invert(100%)"
                               :position "absolute"
                               :bottom "12px"
                               :right "12px"}}))
           (<> (d/i {:class (card-suits->icon suit)
                     :style {:position "absolute"
                             :top "12px"
                             :right "12px"
                             :font-size "36px"}})
               (d/i {:class (card-suits->icon suit)
                     :style {:position "absolute"
                             :bottom "12px"
                             :left "12px"
                             :font-size "36px"}}))))) ; no centro o valor, nas duas pontas

(defnc discard-pile-component [{:keys [count]}]
  (d/article {:class "discard"
              :style {:position "relative"}}
             (d/p {:style {:position "absolute"
                           :top "-20px"
                           :left "-12px"
                           :font-size "36px"}}
                  (if (= 1 count) "" (str count)))
             (d/div {:style {:border "4px solid var(--main-color)"
                             :background-color "var(--main-bg-color)"
                             :border-radius "16px"
                             :padding "8px"
                             :justify-content "center"
                             :align-items "center"
                             :display "flex"
                             :flex-direction "column"
                             :width "7rem"
                             :height "9rem"
                             :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color), 
                               -12px 12px 0 0 var(--main-color), 
                               -18px 20px 0 0 var(--main-bg-color), 
                               -22px 24px 0 0 var(--main-color)"}}
                    (d/i {:class "icon-trash-2" :style {:font-size "54px"}}))))

(defn ^:private draw-card
  [state set-state]
  (when (< (-> state :hand count) 5)
    (->> (actions/take-cards-from-deck state)
         (_set-game-state set-state))))

(defnc deck-component [{:keys [count state set-state]}]
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
                         :on-click #(draw-card state set-state)
                         :style {:margin "28px 0 0"
                                 :background-color "var(--secondary-color)"
                                 :color "var(--text-color)"
                                 :border "none"
                                 :border-radius "12px"
                                 :padding "8px"
                                 :font-weight "bold"
                                 :max-height "36px"
                                 :min-width "120px"}}
                        "Draw")))

(defn hand-cards [hand]
  (when (< 0 (count hand))
    (d/article {:class "hand"}
               (d/p "Hand")
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"}}
                      (for [card hand]
                        (d/div {:style {:padding "8px 0px"
                                        :display "flex"
                                        :column-gap "16px"}}
                               ($ card-component {:rank (:rank card) :suit (:suit card)})))))))

(defnc app []
  (let [[game-state set-game-state] (hooks/use-state {:deck [], :hand [], :discard-pile []})
        start-game (fn [] (_set-game-state set-game-state (new-game true)))
        empty-state?  (-> game-state :deck empty?)]
    (d/div
     (d/header {:class "header" :style {:display "flex"
                                        :justify-content "space-between"
                                        :align-items "center"
                                        :margin "12px 12px 48px"}}
               (d/h1 "Ace of Cards - Fabula Ultima")
               (d/button  {:id "start-button"
                           :on-click #(start-game);TODO: criar alerta de confirmação quando for reiniciar
                           :style {:background-color  (if empty-state? "var(--secondary-color)" "var(--main-color)")
                                   :color "var(--text-color)"
                                   :border "none"
                                   :border-radius "12px"
                                   :padding "8px"
                                   :font-weight "bold"
                                   :max-height "36px"
                                   :min-width "120px"}}
                          (if empty-state? "New Game" "Reset Game")))
     (when (not empty-state?)
       (d/div {:style {:display "flex" :justify-content "space-between" :align-items "center"}}
              (d/main  {:style {:align-self "start"}}
                       (hand-cards (:hand game-state)))
              (d/aside {:style {:display "flex"
                                :align-items "center"
                                :justify-content "center"
                                :flex-direction "column"
                                :align-self "center"
                                :height "80vh"
                                :gap "200px"}}
                       ($ deck-component {:count (-> game-state :deck count)
                                          :state game-state
                                          :set-state set-game-state})
                       ($ discard-pile-component  {:count (-> game-state :discard-pile count)})))))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
