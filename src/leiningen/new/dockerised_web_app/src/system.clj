(ns {{ns-name}}.system
  (:require [com.stuartsierra.component :as component]
            [metrics.core :refer [new-registry]]
            [metrics.jvm.core :as jvm]
            [{{ns-name}}.graphite-reporter :refer [new-graphite-reporter]]
            [{{ns-name}}.logging]
            [{{ns-name}}.web-server :refer [new-web-server]]))

(def components [:web-server :metrics-registry :graphite-reporter])

(defrecord Quotations-Web-System []
  component/Lifecycle
  (start [this]
    (component/start-system this components))
  (stop [this]
    (component/stop-system this components)))

(defn new-{{ns-name}}-system
  "Constructs the component system for the application."
  []
  (let [metrics-registry (new-registry)]
    (jvm/instrument-jvm metrics-registry)
    (map->Quotations-Web-System
      {:web-server        (component/using (new-web-server)
                                           [:metrics-registry])
       :graphite-reporter (component/using (new-graphite-reporter)
                                           [:metrics-registry])
       :metrics-registry  metrics-registry})))
