(ns {{ns-name}}.views.home
  (:require [clostache.parser :refer [render-resource]]
            [{{ns-name}}.views.shared :refer [wrap-with-layout]]))

(def home-path (partial format "templates/home/%s.mustache"))

(defn about-view
  [model]
  (let [content (render-resource (home-path "about") model)]
    (wrap-with-layout "home" content)))
