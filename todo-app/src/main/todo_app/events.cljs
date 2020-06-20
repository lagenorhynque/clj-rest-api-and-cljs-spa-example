(ns todo-app.events
  (:require [re-frame.core :as re-frame]
            [todo-app.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-current-route
 (fn [db [_ route]]
   (assoc db :route route)))
