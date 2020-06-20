(ns todo-api.handler.todo
  (:require [ataraxy.core :as ataraxy]
            [ataraxy.response :as response]
            [integrant.core :as ig]))

(def todos
  (atom {1 {:id 1
            :task "build an API"}
         2 {:id 2
            :task "?????"}
         3 {:id 3
            :task "profit!"}}))

(defmethod ig/init-key ::list-todos [_ {:keys [db]}]
  (fn [_]
    ;; TODO: DBアクセス
    [::response/ok (vals @todos)]))

(defmethod ig/init-key ::create-todo [_ {:keys [db]}]
  (fn [{[_ todo] :ataraxy/result}]
    ;; TODO: DBアクセス
    (let [todo-id (->> @todos
                       keys
                       (apply max)
                       inc)]
      (swap! todos assoc todo-id (merge {:id todo-id}
                                        (select-keys todo [:task])))
      [::response/created (str "/todos/" todo-id) (get @todos todo-id)])))

(defmethod ig/init-key ::fetch-todo [_ {:keys [db]}]
  (fn [{[_ todo-id] :ataraxy/result}]
    ;; TODO: DBアクセス
    (if-let [todo (get @todos todo-id)]
      [::response/ok todo]
      [::response/not-found {:message (str "Todo " todo-id " doesn't exist")}])))

(defmethod ig/init-key ::delete-todo [_ {:keys [db]}]
  (fn [{[_ todo-id] :ataraxy/result}]
    ;; TODO: DBアクセス
    (if (get @todos todo-id)
      (do (swap! todos dissoc todo-id)
          [::response/no-content])
      [::response/not-found {:message (str "Todo " todo-id " doesn't exist")}])))

(defmethod ig/init-key ::update-todo [_ {:keys [db]}]
  (fn [{[_ todo-id todo] :ataraxy/result}]
    ;; TODO: DBアクセス
    (swap! todos assoc todo-id (merge {:id todo-id}
                                      (select-keys todo [:task])))
    [::response/created (str "/todos/" todo-id) (get @todos todo-id)]))
