(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.responses :refer [json-ok]]
            [{{ns-name}}.components.mongodb.core :refer [find-one insert]]
            [ring.util.response :refer [not-found redirect-after-post]]))

(defn index-get
  [{:keys [mongodb]}]
  (if-let [model (find-one mongodb "visitors" {})]
    (do (-> model
            (select-keys [:name :location])
            json-ok))
    (not-found {})))

(defn index-post
  [{:keys [mongodb]} {:keys [name location]}]
  (insert mongodb "visitors" {:name name :location location})
  (redirect-after-post "/"))
