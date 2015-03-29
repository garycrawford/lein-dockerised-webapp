(ns {{ns-name}}.views.healthcheck
  (:require [clostache.parser :refer [render-resource]]))

(defn healthcheck-list-view
  [model]
  (render-resource "templates/healthcheck/healthcheck-list.mustache" model))
