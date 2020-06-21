(ns todo-app.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::current-route
 (fn [db _]
   (get db :route {:handler :todo-app.views/home})))

(re-frame/reg-sub
 ::todos
 (fn [db _]
   (:todos db)))

(re-frame/reg-sub
 ::selected-todo
 (fn [db _]
   (:selected-todo db)))
