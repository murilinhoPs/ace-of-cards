(ns app.components.cards.card-component
  (:require ["lucide-react/dynamic" :refer [DynamicIcon]]
            [app.adapters.card-suit :refer [card-suits->icon]]
            [app.i18n]
            [app.utils :refer [asset]]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]))

(defn ^:private translate-card-rank [rank]
  (if (= "Às" rank) (app.i18n/app-tr [:ace-card/name]) rank))

(defnc display-suit
  [{:keys [suit top bottom left right]}]
  (d/div {:style {:position "absolute"
                  :top top
                  :bottom bottom
                  :right right
                  :left left}}
         ($ DynamicIcon {:class (card-suits->icon suit)
                         :name (card-suits->icon suit)
                         :size "clamp(1rem, 2vw + 1rem, 2rem)" })))

(defnc card-component
  [{:keys [rank suit on-click]}]
  (d/button  {:on-click (if (nil? on-click) #() #(on-click))
              :class "hand-card-button"
              :style {:border "3.6px solid var(--lighter-color)"
                      :color "var(--text-color)"
                      :border-radius "16px"
                      :padding "8px"
                      :justify-content "center"
                      :align-items "center"
                      :display "flex"
                      :flex-direction "column"
                      :width "clamp(5.6rem, 14vw + 1rem, 8.6rem)"
                      :height "clamp(7.6rem, 16vw + 3rem, 12rem)"
                      :position "relative"}}
             (d/p {:style {:font-size "clamp(1.2rem, 2.4vw + 1rem, 2.4rem)" 
                           :font-weight "600"}} 
                  (translate-card-rank rank))
             (if (= :joker suit)
               (<> (d/img {:src (asset "joker.png")
                           :width "28px"
                           :style {:filter "invert(100%)"
                                   :position "absolute"
                                   :top "12px"
                                   :left "12px"}})
                   (d/img {:src (asset "joker.png")
                           :width "28px"
                           :style {:filter "invert(100%)"
                                   :position "absolute"
                                   :bottom "12px"
                                   :right "12px"}}))
               (<> ($ display-suit {:suit suit
                                    :top "12px"
                                    :right "12px"})
                   ($ display-suit {:suit suit
                                    :bottom "8px"
                                    :left "12px"})))))