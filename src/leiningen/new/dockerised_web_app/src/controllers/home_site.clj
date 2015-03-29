(ns {{ns-name}}.controllers.home
  (:require [{{ns-name}}.models.home :refer [about-model]]
            [{{ns-name}}.views.home :refer [about-view]]))

(defn ok
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn index
  []
  (let [model (about-model)
        view (about-view model)]
    (ok view)))
