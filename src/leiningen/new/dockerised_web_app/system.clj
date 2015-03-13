(ns {{ns-name}}.system   
  (:require                    
    [com.stuartsierra.component :as component]
    [{{ns-name}}.web-server :refer [new-web-server]]
    [{{ns-name}}.graphite-reporter :refer [new-graphite-reporter]]
    [metrics.core :refer [new-registry]]))

(def components [:web-server :metrics-registry :graphite-reporter])

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
    {:web-server        (component/using (new-web-server)
                                         [:metrics-registry])
     :graphite-reporter (component/using (new-graphite-reporter)
                                         [:metrics-registry])
     :metrics-registry  (new-registry)}))
