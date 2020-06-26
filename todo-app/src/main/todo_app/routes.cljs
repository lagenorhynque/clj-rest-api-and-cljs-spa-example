(ns todo-app.routes
  (:require [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :todo-app.views/list
        "create" :todo-app.views/create
        ["edit/" [ #"\d+" :id ]] :todo-app.views/edit}])

(def path-for (partial bidi/path-for routes))

(defn navigate
  ([view] (navigate view {}))
  ([view params]
   (accountant/navigate! (apply path-for view (apply concat params)))))
