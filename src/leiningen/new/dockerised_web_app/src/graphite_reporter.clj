(ns {{ns-name}}.graphite-reporter
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [metrics.reporters.graphite :as graphite]
            [schema.core :as s]
            [schema.utils :as u]
            [schema.coerce :as c]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer  [prepend append]])
  (:import (com.codahale.metrics MetricFilter)
           (java.util.concurrent TimeUnit)))

(defn create-prefix
  [{:keys [app-name hostname]}]
  (format "stats.timers.%1s.%2s" app-name hostname))

(def base-config {:rate-unit     TimeUnit/SECONDS
                  :duration-unit TimeUnit/MILLISECONDS
                  :filter        MetricFilter/ALL})

(def app-name-regex #"[a-zA-Z0-9\-]+")
(def hostname-regex #"[a-zA-Z0-9]")

(def GraphiteReporterEnvVars {(s/required-key :host)     s/Str
                              (s/required-key :port)     s/Int
                              (s/required-key :app-name) #"[a-zA-Z0-9]+"
                              (s/required-key :hostname) #"[a-zA-Z0-9]+"})

(def parse-env-vars (c/coercer GraphiteReporterEnvVars
                               c/string-coercion-matcher))

(defn graphite-reporter-env-vars
  []
  (parse-env-vars {:host     (env :graphite-host)
                   :port     (env :graphite-port)
                   :app-name (env :app-name)
                   :hostname (env :hostname)}))

(defn graphite-reporter-config
  []
  (let [env-vars (graphite-reporter-env-vars)]
    (when-not (u/error? env-vars)
      (-> env-vars
          (select-keys [:host :port])
          (assoc :prefix (create-prefix env-vars))
          (merge base-config)))))

(defn generate-reporter
  [{:keys [metrics-registry]}]
  (when-let [config (graphite-reporter-config)]
    (graphite/reporter metrics-registry config)))

(defn start-reporter
  [this]
  (let [reporter (generate-reporter this)]
    (graphite/start reporter 10)
    (assoc this :graphite-reporter reporter)))

(defn start
  [{:keys [graphite-reporter] :as this}]
  (if graphite-reporter
    graphite-reporter
    (start-reporter this)))

(defn stop
  [{:keys [graphite-reporter] :as this}]
  (when graphite-reporter (graphite/stop graphite-reporter))
  this)

(defrecord GraphiteReporter [metrics-registry]
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-graphite-reporter []
  (map->GraphiteReporter {}))

(prepend start  (info :metrics-reporter :starting))
(append  start  (info :metrics-reporter :started))
(prepend stop   (info :metrics-reporter :stoping))
(append  stop   (info :metrics-reporter :stopped))
