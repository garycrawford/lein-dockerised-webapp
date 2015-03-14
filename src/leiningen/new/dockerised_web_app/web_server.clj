(ns {{ns-name}}.web-server
  (:require [com.stuartsierra.component :as component]
            [metrics.ring.instrument :as ring]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [api-defaults
                                              wrap-defaults]]
            [ring.middleware.json :as json-response]
            [ring.util.response :as util]
            [scenic.routes :as scenic]
            [taoensso.timbre :refer [info]]))

(defn create-handler
  [metrics-registry routes routes-map]
  (-> (scenic/scenic-handler routes routes-map)
      (json-response/wrap-json-response)
      (wrap-defaults api-defaults)
      (ring/instrument metrics-registry)))

(def routes-map
  {:home (fn [req] (util/response {:msg "home place holder"}))
   :healthcheck (fn [req] (util/response {:msg "healthcheck place holder"}))})

(def routes (scenic/load-routes-from-file "routes.txt"))

(defrecord WebServer [metrics-registry]
  component/Lifecycle
  (start [this]
    (let [handler (create-handler metrics-registry routes routes-map)]
      (assoc this :server (jetty/run-jetty handler {:port 1234 :join? false})
                  :handler handler)))
  (stop [this]
    (.stop (:server this))
    (dissoc this :server)))

(defn new-web-server []
  (map->WebServer {}))
