(ns {{ns-name}}.components.mongo-connection
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]))

(defn start
  [{:keys [conn] :as this}]
  (if conn
    this
    (let [uri (env :mongodb-uri)
          {:keys [conn]} (mg/connect-via-uri uri)]
      (assoc this :conn conn))))

(defn stop
  [{:keys [conn] :as this}]
  (if conn
   (do
     (mg/disconnect conn)
     (dissoc this :conn))
   this))

(defrecord MongoConnection []
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-mongo-connection
  []
  (map->MongoConnection {}))

(prepend start (info :mongo-connection :connecting))
(append  start (info :mongo-connection :connected))
(prepend stop  (info :mongo-connection :disconnecting))
(append  stop  (info :mongo-connection :disconnected))
