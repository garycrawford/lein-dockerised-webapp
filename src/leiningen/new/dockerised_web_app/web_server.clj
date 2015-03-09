(ns {{ns-name}}.web-server 
  (:require                    
    [com.stuartsierra.component :as component]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.json :as json-response]
    [scenic.routes :as scenic]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.util.response :as util]))

(defn create-handler           
  [routes routes-map]
  (-> (scenic/scenic-handler routes routes-map)
      (json-response/wrap-json-response)
      (wrap-defaults api-defaults))) 

(def routes-map               
  {:home (fn [req] (util/response {:msg "home place holder"}))
   :healthcheck (fn [req] (util/response {:msg "healthcheck place holder"}))}) 

(def routes (scenic/load-routes-from-file "routes.txt"))

(defrecord WebServer []
  component/Lifecycle          
  (start [this]                
    (let [handler (create-handler routes routes-map)]
      (assoc this :server (jetty/run-jetty handler {:port 1234 :join? false}))))
  (stop [this]
    (.stop (:server this))     
    (dissoc this :server)))    

(defn new-web-server []        
  (map->WebServer {}))
