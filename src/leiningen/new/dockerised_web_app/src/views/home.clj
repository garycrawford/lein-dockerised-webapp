(ns {{ns-name}}.views.home
  (:require [clostache.parser :refer [render-resource]]))

(defn about-view
  [model]
  (render-resource "templates/home/about.mustache" model))
