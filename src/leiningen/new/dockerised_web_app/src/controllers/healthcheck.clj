(ns {{ns-name}}.controllers.healthcheck
  (:require [{{ns-name}}.models.healthcheck :refer [healthcheck-list-model]]
            [{{ns-name}}.views.healthcheck :refer [healthcheck-view]]))

(defn ok
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn index
  []
  (ok {:model (healthcheck-list-model)
       :view  (healthcheck-view "healthcheck-list")}))
