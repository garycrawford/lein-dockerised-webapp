(ns {{ns-name}}.system   
  (:require                    
    [com.stuartsierra.component :as component]
    [{{ns-name}}.web-server :as web-server]))

(def components [:web-server])

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
    {:web-server (web-server/new-web-server)}))
