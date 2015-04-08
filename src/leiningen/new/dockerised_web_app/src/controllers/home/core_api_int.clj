(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.models.home :refer [about-model about-model!]]
            [{{ns-name}}.responses :refer [json-ok]]
            [ring.util.response :refer [not-found redirect-after-post]]))

(defn index-get
  []
  (if-let [model (about-model)]
    (json-ok model) 
    (not-found {})))

(defn index-post
  [{:keys [name location]}]
  (about-model! {:name name :location location})
  (redirect-after-post "/"))
