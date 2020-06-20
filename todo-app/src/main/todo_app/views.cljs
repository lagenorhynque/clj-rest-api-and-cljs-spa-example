(ns todo-app.views
  (:require [re-frame.core :as re-frame]
            [todo-app.subs :as subs]))

(defmulti view :handler)

(defmethod view ::home [_]
  [:div "Home"])

(defmethod view ::list [_]
  [:div "Todo List"])

(defmethod view ::create [_]
  [:div "Create New Todo"])

(defmethod view ::edit [{:keys [route-params]}]
  [:div (str "Edit Todo " (:id route-params))])

(defmethod view :default [_]
  [:div "404 Not Found"])

(defn main-panel []
  [:div "Todo App"
   [view @(re-frame/subscribe [::subs/current-route])]])
