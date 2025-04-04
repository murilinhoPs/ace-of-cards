(ns app.components.modal.modal-base
  (:require ["lucide-react" :refer [X]]
            [app.i18n]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc modal-base [{:keys [game set-show-modal confirm-click content]}]
  (let [close-modal #(set-show-modal assoc :show? false)
        [continue? set-continue?] (hooks/use-state true)
        confirm-action (fn [] (confirm-click) (close-modal))
        heading' (app.i18n/app-tr [:modal/heading])
        confirm' (app.i18n/app-tr [:modal/ok])
        cancel' (app.i18n/app-tr [:modal/cancel])]
    (<>
     (d/div {:class "dark-BG" :on-click close-modal})
     (d/div {:class "centered"}
            (d/div {:class "modal"}
                   (d/div {:class "modal-header"}
                          (d/h3 {:class "heading"}
                                heading'))
                   (d/button {:class "close-btn"  :on-click close-modal}
                             ($ X {:name "icon-x"}))
                   (d/div {:class "modal-content"}
                          ($ content {:set-continue? set-continue?
                                      :game game}))
                   (d/div {:class "modal-actions"}
                          (d/div {:class "actions-container"}
                                 (d/button {:class "cancel-btn"
                                            :on-click close-modal}
                                           cancel')
                                 (d/button {:class "confirm-btn"
                                            :on-click confirm-action
                                            :disabled (not continue?)}
                                           confirm'))))))))
