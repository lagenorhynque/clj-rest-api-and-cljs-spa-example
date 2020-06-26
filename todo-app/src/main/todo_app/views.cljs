(ns todo-app.views
  (:require ["@material-ui/core/AppBar" :default AppBar]
            ["@material-ui/core/Avatar" :default Avatar]
            ["@material-ui/core/Breadcrumbs" :default Breadcrumbs]
            ["@material-ui/core/Button" :default Button]
            ["@material-ui/core/Container" :default Container]
            ["@material-ui/core/Dialog" :default Dialog]
            ["@material-ui/core/DialogActions" :default DialogActions]
            ["@material-ui/core/DialogTitle" :default DialogTitle]
            ["@material-ui/core/Fab" :default Fab]
            ["@material-ui/core/IconButton" :default IconButton]
            ["@material-ui/core/Link" :default Link]
            ["@material-ui/core/List" :default List]
            ["@material-ui/core/ListItem" :default ListItem]
            ["@material-ui/core/ListItemAvatar" :default ListItemAvatar]
            ["@material-ui/core/ListItemSecondaryAction" :default ListItemSecondaryAction]
            ["@material-ui/core/ListItemText" :default ListItemText]
            ["@material-ui/core/TextField" :default TextField]
            ["@material-ui/core/Toolbar" :default Toolbar]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/icons/Add" :default AddIcon]
            ["@material-ui/icons/Delete" :default DeleteIcon]
            ["@material-ui/icons/Save" :default SaveIcon]
            ["@material-ui/icons/Work" :default WorkIcon]
            [fork.core :as fork]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [todo-app.events :as events]
            [todo-app.routes :as routes]
            [todo-app.subs :as subs]))

(defmulti view :handler)

(defmethod view ::list [_]
  [:div
   [:p "Todo List"]
   [:> List
    (map (fn [{:keys [id task]}]
           ^{:key id}
           [:> ListItem {:button true
                         :on-click #(re-frame/dispatch [::events/navigate ::edit {:id id}])}
            [:> ListItemAvatar
             [:> Avatar
              [:> WorkIcon]]]
            [:> ListItemText {:primary task}]
            [:> ListItemSecondaryAction
             [:> IconButton {:edge "end"
                             :aria-label "delete"
                             :on-click #(re-frame/dispatch [::events/open-delete-dialog id])}
              [:> DeleteIcon]]]])
         @(re-frame/subscribe [::subs/todos]))]
   [:> Dialog {:open @(re-frame/subscribe [::subs/delete-dialog-open?])
               :on-close #(re-frame/dispatch [::events/close-delete-dialog])
               :aria-labelledby "alert-dialog-title"}
    [:> DialogTitle {:id "alert-dialog-title"}
     "Are you sure you want to delete this?"]
    [:> DialogActions
     [:> Button {:on-click #(re-frame/dispatch [::events/close-delete-dialog])
                 :color "primary"}
      "Cancel"]
     [:> Button {:on-click (fn []
                             (re-frame/dispatch [::events/delete-todo-by-id
                                                 @(re-frame/subscribe [::subs/delete-target])])
                             (re-frame/dispatch [::events/close-delete-dialog]))
                 :color "secondary"
                 :variant "contained"}
      "Delete"]]]
   [:> Fab {:color "secondary"
            :aria-label "add"
            :on-click #(re-frame/dispatch [::events/navigate ::create])}
    [:> AddIcon]]])

(defn todo-form [props]
  [fork/form (merge {:prevent-default? true
                     :clean-on-unmount? true}
                    props)
   (fn [{:keys [values
                form-id
                handle-change
                handle-blur
                submitting?
                handle-submit]}]
     [:form {:id form-id
             :on-submit handle-submit}
      [:> TextField {:label "Task"
                     :required true
                     :name "task"
                     :default-value (values "task")
                     :on-change handle-change
                     :on-blur handle-blur}]
      [:> Button {:type "submit"
                  :disabled submitting?
                  :color "secondary"
                  :variant "contained"
                  :start-icon (reagent/as-element [:> SaveIcon])}
       "Save"]])])

(defmethod view ::create [_]
  [:div
   [:p "Create New Todo"]
   [todo-form {:on-submit #(re-frame/dispatch [::events/create-todo %])}]])

(defmethod view ::edit [{:keys [route-params]}]
  [:div
   [:p (str "Edit Todo (id: " (:id route-params) ")")]
   (when-let [{:keys [id task]} @(re-frame/subscribe [::subs/selected-todo])]
     [todo-form {:initial-values {"task" task}
                 :on-submit #(re-frame/dispatch [::events/update-todo-by-id id %])}])])

(defmethod view :default [_]
  [:div
   [:p "404 Not Found"]])

(defn main-panel []
  (let [{:keys [handler]
         :as current-route} @(re-frame/subscribe [::subs/current-route])]
    [:div
     [:> AppBar {:position "static"}
      [:> Toolbar
       [:> Breadcrumbs {:aria-label "breadcrumb"}
        [:> Link {:color "inherit"
                  :variant "h6"
                  :href (routes/path-for ::list)}
         "TODOS"]
        (case handler
          ::create [:> Typography {:color "textPrimary"
                                   :variant "h6"}
                    "CREATE"]
          ::edit [:> Typography {:color "textPrimary"
                                 :variant "h6"}
                  "EDIT"]
          nil)]]]
     [:> Container
      [view current-route]]]))
