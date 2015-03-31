(ns {{ns-name}}.controllers.home
  (:require [{{ns-name}}.models.home :refer [about-model]]
            [{{ns-name}}.views.home :refer [home-view]]))

(defn ok
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn index
  []
  (ok {:model (about-model)
       :view  (home-view "about")}))
