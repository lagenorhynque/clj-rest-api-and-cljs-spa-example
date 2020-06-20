(ns todo-api.handler.todo
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [todo-api.boundary.db.todo :as db.todo]))

(defmethod ig/init-key ::list-todos [_ {:keys [db]}]
  (fn [_]
    [::response/ok (db.todo/find-todos db)]))

(defmethod ig/init-key ::create-todo [_ {:keys [db]}]
  (fn [{[_ todo] :ataraxy/result}]
    (let [todo-id (db.todo/create-todo! db todo)]
      [::response/created (str "/todos/" todo-id) (db.todo/find-todo-by-id db todo-id)])))

(defmethod ig/init-key ::fetch-todo [_ {:keys [db]}]
  (fn [{[_ todo-id] :ataraxy/result}]
    (if-let [todo (db.todo/find-todo-by-id db todo-id)]
      [::response/ok todo]
      [::response/not-found {:message (str "Todo " todo-id " doesn't exist")}])))

(defmethod ig/init-key ::delete-todo [_ {:keys [db]}]
  (fn [{[_ todo-id] :ataraxy/result}]
    (if (db.todo/find-todo-by-id db todo-id)
      (do (db.todo/delete-todo! db todo-id)
          [::response/no-content])
      [::response/not-found {:message (str "Todo " todo-id " doesn't exist")}])))

(defmethod ig/init-key ::update-todo [_ {:keys [db]}]
  (fn [{[_ todo-id todo] :ataraxy/result}]
    (db.todo/upsert-todo! db todo-id todo)
    [::response/created (str "/todos/" todo-id) (db.todo/find-todo-by-id db todo-id)]))
