(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [app.utils :refer [asset]]
            [clj.ace-of-cards.card :as card]
            [clj.ace-of-cards.core :refer [new-game]]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [schema.core :as s]))

(defn ^:private set-game-state [set-state]
  (let [data (new-game true)]
    (set-state assoc :hand (:hand data))
    (set-state assoc :deck (:deck data))
    (set-state assoc :discard-pile (:discard-pile data))))

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

(defnc discard-pile-component []
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
                  :position "relative"
                  :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color), 
                               -12px 12px 0 0 var(--main-color), 
                               -18px 20px 0 0 var(--main-bg-color), 
                               -22px 24px 0 0 var(--main-color)"}}
         (d/i {:class "icon-trash-2" :style {:font-size "54px"}})))

(defnc deck-component []
  (d/div {:style {:border "4px solid var(--secondary-color)"
                  :background-color "var(--main-bg-color)"
                  :border-radius "16px"
                  :padding "8px"
                  :justify-content "center"
                  :align-items "center"
                  :display "flex"
                  :flex-direction "column"
                  :width "7rem"
                  :height "9rem"
                  :position "relative"
                  :box-shadow "-8px 10px 0 -2.5px var(--main-bg-color), 
                               -12px 12px 0 0 var(--secondary-color), 
                               -18px 20px 0 0 var(--main-bg-color), 
                               -22px 24px 0 0 var(--secondary-color)"}}
         (d/i {:class "icon-layers" :style {:font-size "54px"}})))

(defn hand-cards [hand]
  (when (< 0 (count hand))
    (d/div {:style {:display "flex"
                    :flex-direction "row"
                    :flex-wrap "wrap"
                    :gap "16px"}}
           (for [card hand]
             (d/div {:style {:padding "8px 0px"
                             :display "flex"
                             :column-gap "16px"}}
                    ($ card-component {:rank (:rank card) :suit (:suit card)}))))))

(defnc app []
  (let [[state set-state] (hooks/use-state {:hand [], :deck [], :discard-pile []})
        start-game (fn [] (set-game-state set-state))
        empty-state?  (-> state :deck empty?)]
    (d/div
     (d/header {:class "header" :style {:display "flex"
                                        :justify-content "space-between"
                                        :align-items "center"
                                        :margin "12px"}}
               (d/h1 "Ace of Cards - Fabula Ultima")
               (d/button  {:id "start-button"
                           :on-click #(start-game);TODO: criar alerta de confirmação quando for reiniciar
                           :style {:background-color  (if empty-state?
                                                        "var(--secondary-color)"
                                                        "var(--main-color)")
                                   :color "var(--text-color)"
                                   :border "none"
                                   :border-radius "12px"
                                   :padding "8px"
                                   :font-weight "bold"
                                   :max-height "36px"
                                   :min-width "120px"}}
                          (if empty-state? "New Game" "Reset Game")))
     (when (not empty-state?)
       (d/main
        (hand-cards (:hand state))
        (d/aside {:style {:position "relative"}}
                 (d/div {:style {:position "absolute", :bottom "28px", :right "20px"}}
                        ($ discard-pile-component))
                 (d/div {:style {:position "absolute", :bottom "456px", :right "20px"}}
                        ($ deck-component))))))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
