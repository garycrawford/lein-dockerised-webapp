(ns {{ns-name}}.system   
  (:require                    
    [com.stuartsierra.component :as component]
    [{{ns-name}}.web-server :as web-server]
    [{{ns-name}}.metrics :as metrics]
    [environ.core :refer [env]]))

(def components [:web-server :metrics])

(defrecord Quotations-Web-System []
  component/Lifecycle          
  (start [this]                
    (component/start-system this components))
  (stop [this]                 
    (component/stop-system this components)))

(defn new-{{ns-name}}-system   
  "Constructs a component system" 
  []
  (map->Quotations-Web-System
    {:web-server (web-server/new-web-server)
     :metrics (metrics/new-metrics (env :graphite-host)
                                   (env :graphite-port)
                                   (env :graphite-prefix))}))
