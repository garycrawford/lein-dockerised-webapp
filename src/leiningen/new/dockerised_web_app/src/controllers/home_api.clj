(ns {{ns-name}}.controllers.home
  (:require [{{ns-name}}.models.home :refer [about-model]]))

(defn ok
  [body]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body body})

(defn index
  []
  (ok (about-model)))
