(ns todo-api.middleware
  (:require [integrant.core :as ig]
            [ring.middleware.cors :refer [wrap-cors]]))

(defmethod ig/init-key ::wrap-cors [_ config]
  #(apply wrap-cors % (apply concat config)))
