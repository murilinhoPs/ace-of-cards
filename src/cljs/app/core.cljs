(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [app.components.modal.card-options :as card.option]
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
  (set-state assoc :table (:table data))
  (set-state assoc :discard-pile (:discard-pile data)))

(s/defn ^:private card-suits->icon :- s/Str
  [suit :- card/Suit]
  (cond
    (= :hearts suit) "icon-heart"
    (= :spades suit) "icon-spade"
    (= :clubs suit) "icon-club"
    (= :diamonds suit) "icon-diamond"))

(defn ^:private _restart-game
  [set-state restart-fn]
  (set-state {:show? true
              :confirm-click restart-fn
              :content #(d/p "Are you sure you want to restart game?")}))

(defn ^:private draw-card
  [state set-state]
  (when (< (-> state :hand count) 5)
    (->> (actions/take-cards-from-deck state)
         (_set-game-state set-state))))

(defn ^:private shuffle-deck
  [state set-state]
  (->> (actions/shuffle-deck state)
       (_set-game-state set-state)))

(defnc card-component
  [{:keys [rank suit on-click]}]
  (d/button  {:on-click (if (nil? on-click) #() #(on-click))
              :class "hand-card-button"
              :style {:border "4px solid var(--lighter-color)"
                      :color "var(--text-color)"
                      :border-radius "16px"
                      :padding "8px"
                      :justify-content "center"
                      :align-items "center"
                      :display "flex"
                      :flex-direction "column"
                      :width "10rem"
                      :height "14rem"
                      :position "relative"}}
             (d/p {:style {:font-size "3rem"}} rank)
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
                                 :font-size "36px"}})))))

(defnc discard-pile-component [{:keys [count]}]
  (d/article {:class "discard"
              :style {:position "relative"}}
             (d/p {:style {:position "absolute"
                           :top "-20px"
                           :left "-12px"
                           :font-size "36px"}}
                  count)
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

(defnc deck-component [{:keys [count game-state set-game-state]}]
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
                         :on-click #(draw-card game-state set-game-state)
                         :style {:margin "2rem 0 0"
                                 :background-color "var(--secondary-color)"
                                 :color "var(--text-color)"
                                 :border "none"
                                 :border-radius "12px"
                                 :padding "8px"
                                 :font-weight "bold"
                                 :max-height "36px"
                                 :min-width "120px"}}
                        "Draw")))

(defn hand-cards [hand & {:keys [card-click]}]
  (d/article {:class "hand" :style {:min-height "340px"}}
             (d/h3 "Hand")
             (when (< 0 (count hand))
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"}}
                      (for [card hand]
                        (d/div {:key (:id card)
                                :style {:padding "8px 0px"
                                        :display "flex"
                                        :column-gap "16px"}}
                               ($ card-component {:rank (:rank card)
                                                  :suit (:suit card)
                                                  :on-click #(card-click card)})))))))

(defn table-cards [table & {:keys [card-click]}]
  (d/article {:class "table"}
             (d/h3 "Table")
             (when (< 0 (count table))
               (d/div {:style {:display "flex"
                               :flex-direction "row"
                               :flex-wrap "wrap"
                               :gap "16px"
                               :align-self "center"}}
                      (for [card table]
                        (d/div {:key (:id card)
                                :style {:padding "8px 0px"
                                        :display "flex"
                                        :column-gap "16px"}}
                               ($ card-component {:rank (:rank card)
                                                  :suit (:suit card)})))))))

(defnc modal [{:keys [set-show-modal confirm-click content]}]
  (let [close-modal #(set-show-modal assoc :show? false)
        [continue? set-continue?] (hooks/use-state true)
        confirm-action (fn [] (confirm-click) (close-modal))]
    (<>
     (d/div {:class "dark-BG" :on-click close-modal})
     (d/div {:class "centered"}
            (d/div {:class "modal"}
                   (d/div {:class "modal-header"}
                          (d/h3 {:class "heading"} "Action Required"))
                   (d/button {:class "close-btn"  :on-click close-modal}
                             (d/i {:class "icon-x"}))
                   (d/div {:class "modal-content"}
                          ($ content {:set-continue? set-continue?}))
                   (d/div {:class "modal-actions"}
                          (d/div {:class "actions-container"}
                                 (d/button {:class "cancel-btn"
                                            :on-click close-modal}
                                           "Cancel")
                                 (d/button {:class "confirm-btn"
                                            :on-click confirm-action
                                            :disabled (not continue?)}
                                           "Confirm"))))))))

(defnc app []
  (let [[started? set-started?] (hooks/use-state false)
        [game-state set-game-state] (hooks/use-state {:deck [], :hand [], :discard-pile [], :table []})
        [modal-state set-modal-state] (hooks/use-state {:show? false, :content nil, :confirm nil})
        start-game (fn [] (set-started? true)
                     (_set-game-state set-game-state (new-game true)))
        restart-game #(_restart-game set-modal-state start-game)]
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
                                                 :on-click #(shuffle-deck game-state set-game-state)
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
                                  :on-click (if started? #(restart-game) #(start-game))
                                  :style {:background-color  (if started? "var(--main-color)" "var(--secondary-color)")
                                          :color "var(--text-color)"
                                          :border "none"
                                          :border-radius "12px"
                                          :padding "8px"
                                          :font-weight "bold"
                                          :max-height "36px"
                                          :min-width "120px"}}
                                 (if started? "Reset Game" "New Game"))))
     (when started?
       (d/div {:style {:display "flex" :justify-content "space-between" :align-items "center"}}
              (d/main  {:style {:align-self "start"}}
                       (hand-cards (:hand game-state)
                                   {:card-click (fn [card] (set-modal-state
                                                            {:show? true
                                                             :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                             :content card.option/card-options-component}))})
                       (table-cards (:table game-state)))
              (d/aside {:style {:display "flex"
                                :align-items "center"
                                :justify-content "center"
                                :flex-direction "column"
                                :height "80vh"
                                :gap "120px"}}
                       ($ deck-component {:count (-> game-state :deck count)
                                          :game-state game-state
                                          :set-game-state set-game-state})
                       ($ discard-pile-component  {:count (-> game-state :discard-pile count)}))))
     (when (:show? modal-state) ($ modal {:set-show-modal set-modal-state
                                          :content  (:content modal-state)
                                          :confirm-click (:confirm-click modal-state)})))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
