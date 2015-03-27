(ns {{ns-name}}.metrics
  (:require 
    [com.stuartsierra.component :as component]
    [metrics.jvm.core :as jvm]
    [metrics.reporters.graphite :as graphite]
    [metrics.core :refer [new-registry]]
    [clojure.string :refer [blank?]]
    [{{ns-name}}.web-server :as web-server])
  (:import 
    [java.util.concurrent TimeUnit]
    [com.codahale.metrics MetricFilter]))

(defn- generate-reporter
  [reg {:keys [host port prefix]}]
  (graphite/reporter reg {:host host
                          :port port
                          :prefix prefix
                          :rate-unit TimeUnit/SECONDS
                          :duration-unit TimeUnit/MILLISECONDS
                          :filter MetricFilter/ALL}))

(defn- instrument
  [reg web-server]
  (jvm/instrument-jvm reg)
  (web-server/instrument-routes web-server reg))

(defn- init-reporter
  [{web-server :web-server :as this}]
  (let [reg (new-registry)]
    (instrument reg web-server)
    (let [reporter (generate-reporter reg this)]
      (graphite/start reporter 10)
      reporter)))

(defn- convert-port
  [port]
  (when (not (blank? port))
    (java.lang.Integer/parseInt port)))

(defn- start
  [{:keys [gr port host prefix] :as this}]
  (if (not (or (nil? port) (blank? host) (blank? prefix)))
    (if gr
      (do (graphite/start gr 10) this)
      (->> this init-reporter (assoc this :gr)))
    this))

(defn- stop
  [{:keys [gr] :as this}]
  (graphite/stop gr)
  this)

(defrecord Metrics [web-server]
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-metrics [host port prefix]
  (map->Metrics {:host host
                 :port (convert-port port)
                 :prefix prefix}))
