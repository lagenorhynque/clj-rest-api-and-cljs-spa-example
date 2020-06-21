(ns todo-app.views
  (:require [re-frame.core :as re-frame]
            [todo-app.events :as events]
            [todo-app.subs :as subs]))

(defmulti view :handler)

(defmethod view ::home [_]
  [:div "Home"])

(defmethod view ::list [_]
  (re-frame/dispatch [::events/fetch-todos])
  [:div "Todo List"
   [:ul
    (map (fn [{:keys [id task]}]
           [:li {:key id} task])
         @(re-frame/subscribe [::subs/todos]))]])

(defmethod view ::create [_]
  [:div "Create New Todo"])

(defmethod view ::edit [{:keys [route-params]}]
  [:div (str "Edit Todo " (:id route-params))])

(defmethod view :default [_]
  [:div "404 Not Found"])

(defn main-panel []
  [:div "Todo App"
   [view @(re-frame/subscribe [::subs/current-route])]])
