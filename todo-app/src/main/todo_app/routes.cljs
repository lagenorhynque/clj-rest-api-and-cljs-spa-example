(ns todo-app.routes
  (:require [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :todo-app.views/home
        "list" :todo-app.views/list
        "create" :todo-app.views/create
        [[ #"\d+" :id ] "/edit"] :todo-app.views/edit}])

(defn navigate
  ([view] (navigate view {}))
  ([view params]
   (accountant/navigate! (apply bidi/path-for routes view (apply concat params)))))
