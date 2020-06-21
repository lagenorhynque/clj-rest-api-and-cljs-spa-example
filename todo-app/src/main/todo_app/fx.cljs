(ns todo-app.fx
  (:require [re-frame.core :as re-frame]
            [todo-app.routes :as routes]))

(re-frame/reg-fx
 ::navigate
 (fn [{:keys [view params]}]
   (routes/navigate view params)))
