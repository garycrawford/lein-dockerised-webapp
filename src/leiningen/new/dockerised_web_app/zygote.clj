(ns {{ns-name}}.zygote
  (:require
    [com.stuartsierra.component :as component]
    [{{ns-name}}.system :as system])
  (:gen-class))

(def {{ns-name}}-system (system/new-{{ns-name}}-system))

(defn app-init
  []
  (alter-var-root #'{{ns-name}}-system component/start))

(defn -main [& args]
  (app-init))
