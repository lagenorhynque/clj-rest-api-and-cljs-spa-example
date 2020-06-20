(ns todo-api.boundary.db.todo
  (:require [clojure.spec.alpha :as s]
            [duct.database.sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]))

(s/def ::id nat-int?)
(s/def ::task string?)
(s/def ::todo
  (s/keys :req-un [::id
                   ::task]))
(s/def ::row-count nat-int?)

(s/fdef find-todos
  :args (s/cat :db any?)
  :ret (s/coll-of ::todo))

(s/fdef find-todo-by-id
  :args (s/cat :db any?
               :id ::id)
  :ret (s/nilable ::todo))

(s/fdef create-todo!
  :args (s/cat :db any?
               :todo (s/keys :req-un [::task]))
  :ret ::id)

(s/fdef upsert-todo!
  :args (s/cat :db any?
               :id ::id
               :todo (s/keys :req-un [::task]))
  :ret ::id)

(s/fdef delete-todo!
  :args (s/cat :db any?
               :id ::id)
  :ret ::row-count)

(defprotocol Todo
  (find-todos [db])
  (find-todo-by-id [db id])
  (create-todo! [db todo])
  (upsert-todo! [db id todo])
  (delete-todo! [db id]))

(defn ->connectable [db]
  (-> db :spec :datasource))

(def jdbc-opts
  {:return-keys true
   :builder-fn rs/as-unqualified-lower-maps})

(extend-protocol Todo
  duct.database.sql.Boundary
  (find-todos [db]
    (sql/query (->connectable db)
               ["SELECT id, task FROM todo ORDER BY id ASC"]
               jdbc-opts))
  (find-todo-by-id [db id]
    (sql/get-by-id (->connectable db)
                   :todo id jdbc-opts))
  (create-todo! [db todo]
    (-> (->connectable db)
        (sql/insert! :todo (select-keys todo [:task]) jdbc-opts)
        :id))
  (upsert-todo! [db id {:keys [task]}]
    (-> (->connectable db)
        (jdbc/execute-one! ["INSERT INTO todo (id, task) VALUES (?, ?)
                               ON CONFLICT ON CONSTRAINT todo_pkey
                               DO UPDATE SET task = ?"
                            id task task]
                           jdbc-opts)
        :id))
  (delete-todo! [db id]
    (-> (->connectable db)
        (sql/delete! :todo {:id id})
        :next.jdbc/update-count)))
