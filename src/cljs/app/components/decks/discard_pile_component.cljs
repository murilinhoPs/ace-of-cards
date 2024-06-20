(ns app.components.decks.discard-pile-component 
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]))

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
