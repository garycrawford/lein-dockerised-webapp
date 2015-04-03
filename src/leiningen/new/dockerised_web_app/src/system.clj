(ns {{ns-name}}.system
  (:require [com.stuartsierra.component :as component]
            [metrics.core :refer [new-registry]]
            [metrics.jvm.core :as jvm]
            [{{ns-name}}.metrics-reporter :refer [new-metrics-reporter]]
            {{mongodb-system-require}}
;            [{{ns-name}}.components.mongo-connection :refer [new-mongo-connection]]
            [{{ns-name}}.logging-config]
            [{{ns-name}}.web-server :refer [new-web-server]]))

; (def components [:web-server :metrics-registry :metrics-reporter :mongo-connection])
(def components [:web-server :metrics-registry :metrics-reporter{{mongodb-system-components}}])

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
      {:web-server       (component/using (new-web-server)
                                          [:metrics-registry])
       :metrics-reporter (component/using (new-metrics-reporter)
                                          [:metrics-registry])
       :metrics-registry  metrics-registry
      ; :mongo-connection (new-mongo-connection)
       {{mongodb-system-map}}})))
