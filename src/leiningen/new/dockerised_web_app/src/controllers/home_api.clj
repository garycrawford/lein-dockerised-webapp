(ns {{ns-name}}.controllers.home
  (:require [{{ns-name}}.models.home :refer [about-model]]
            [{{ns-name}}.views.home :refer [about-view]]))

(defn ok
  [body]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body body})

(defn index
  []
  (ok {:message "welcome to the api root"}))
