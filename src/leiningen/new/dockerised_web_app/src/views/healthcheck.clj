(ns {{ns-name}}.views.healthcheck
  (:require [clostache.parser :refer [render-resource]]
            [{{ns-name}}.views.shared :refer [wrap-with-layout]]))

(def healthcheck-path (partial format "templates/healthcheck/%s.mustache"))

(defn healthcheck-list-view
  [model]
  (let [content (render-resource (healthcheck-path "healthcheck-list") model)]
    (wrap-with-layout "healthcheck" content)))
