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

(def routes-map
  {:home (fn [req] (util/response {:msg "home place holder"}))
   :healthcheck (fn [req] (util/response {:msg "healthcheck place holder"}))})

(def routes (scenic/load-routes-from-file "routes.txt"))

(def jetty-config {:port 1234 :join? false})

(defn create-handler
  [metrics-registry]
  (-> (scenic/scenic-handler routes routes-map)
      (json-response/wrap-json-response)
      (wrap-defaults api-defaults)
      (ring/instrument metrics-registry)))

(defn start
  [{:keys [metrics-registry server] :as this}]
  (if server
      this
      (do (info "web-server: starting")
          (let [handler (create-handler metrics-registry)
                server  (jetty/run-jetty handler jetty-config)]
            (info "web-server: started")
            (assoc this :server server)))))

(defn stop
  [{:keys [server] :as this}]
  (if server
      (do (info "web-server: stoping")
          (.stop server)
          (.join server)
          (info "web-server: stopped")
          (dissoc this :server))
      this))


(defrecord WebServer [metrics-registry]
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-web-server []
  (map->WebServer {}))
