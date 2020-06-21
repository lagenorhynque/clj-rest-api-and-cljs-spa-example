(ns todo-app.events
  (:require [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :as re-frame]
            [todo-app.config :as config]
            [todo-app.db :as db]
            [todo-app.fx :as fx]))

(def request-defaults
  {:timeout 6000
   :response-format (ajax/json-response-format {:keywords? true})
   :on-failure [::set-error]})

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(defmulti on-navigate (fn [view _] view))
(defmethod on-navigate :todo-app.views/list [_ _]
  {:dispatch [::fetch-todos]})
(defmethod on-navigate :todo-app.views/edit [_ params]
  {:dispatch [::fetch-todo-by-id (:id params)]})
(defmethod on-navigate :default [_ _] nil)

(re-frame/reg-event-fx
 ::set-current-route
 (fn [{:keys [db]} [_ {:keys [handler route-params]
                       :as route}]]
   (merge {:db (assoc db :route route)}
          (on-navigate handler route-params))))

(re-frame/reg-event-fx
 ::navigate
 (fn [_ [_ view params]]
   {::fx/navigate {:view view
                   :params params}}))

(re-frame/reg-event-db
 ::set-error
 (fn [db [_ res]]
   (assoc db :error res)))

(re-frame/reg-event-fx
 ::fetch-todos
 (fn [_ _]
   {:http-xhrio (assoc request-defaults
                       :method :get
                       :uri (str config/API_URL "/todos")
                       :on-success [::set-todos])}))

(re-frame/reg-event-db
 ::set-todos
 (fn [db [_ res]]
   (assoc db
          :todos res
          :selected-todo nil)))

(re-frame/reg-event-fx
 ::fetch-todo-by-id
 (fn [{:keys [db]} [_ todo-id]]
   {:db (assoc db :selected-todo nil)
    :http-xhrio (assoc request-defaults
                       :method :get
                       :uri (str config/API_URL "/todos/" todo-id)
                       :on-success [::set-selected-todo])}))

(re-frame/reg-event-db
 ::set-selected-todo
 (fn [db [_ res]]
   (assoc db :selected-todo res)))

(re-frame/reg-event-fx
 ::create-todo
 (fn [_ [_ {:keys [values]}]]
   {:http-xhrio (assoc request-defaults
                       :method :post
                       :uri (str config/API_URL "/todos")
                       :params values
                       :format (ajax/json-request-format)
                       :on-success [::navigate :todo-app.views/list])}))

(re-frame/reg-event-fx
 ::update-todo-by-id
 (fn [_ [_ todo-id {:keys [values]}]]
   {:http-xhrio (assoc request-defaults
                       :method :put
                       :uri (str config/API_URL "/todos/" todo-id)
                       :params values
                       :format (ajax/json-request-format)
                       :on-success [::navigate :todo-app.views/list])}))

(re-frame/reg-event-fx
 ::delete-todo-by-id
 (fn [_ [_ todo-id]]
   {:http-xhrio (assoc request-defaults
                       :method :delete
                       :uri (str config/API_URL "/todos/" todo-id)
                       :format (ajax/json-request-format)
                       :on-success [::fetch-todos])}))
