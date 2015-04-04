(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]))

(def render (renderer "dockerised-web-app"))

(defn mongodb
  [ns-name]
  {:mongodb-project-dep "[com.novemberain/monger \"2.0.1\"]"
   :mongodb-docker-compose-links "- persistence"
   :mongodb-docker-compose-environment (format "MONGODB_URI: mongodb://192.168.59.103/%1s" ns-name)
   :mongodb-docker-compose-section (->> ["persistence:"
                                         "   image: mongo:3.0.1"
                                         "   ports:"
                                         "   - \"27017:27017\""]
                                        (string/join \newline))})

(defn construct-template
  [lines]
  (->> lines
       (partition 2)
       (map (fn [[line check-fn]] (when (check-fn) line)))
       (filter (complement nil?))
       (string/join \newline)))

(def always (constantly true))
(def mongodb? (partial = :mongodb))

(defn system-ns-str
  [ns-name {:keys [db]}]
  (let [template (-> ["(ns %1$s.system"                                                              always
                      "  (:require [com.stuartsierra.component :as component]"                       always
                      "            [metrics.core :refer [new-registry]]"                             always
                      "            [metrics.jvm.core :as jvm]"                                       always
                      "            [%1$s.metrics-reporter :refer [new-metrics-reporter]]"            always
                      "            [%1$s.components.mongo-connection :refer [new-mongo-connection]]" #(mongodb? db)
                      "            [%1$s.logging-config]"                                            always
                      "            [%1$s.web-server :refer [new-web-server]]))"                      always]
                     construct-template)]
    (format template ns-name)))

(defn system-comp-list-str
  [{:keys [db]}]
  (-> ["(def components [:web-server"         always
       "                 :mongo-connection"   #(mongodb? db)
       "                 :metrics-registry"   always
       "                 :metrics-reporter])" always]
      construct-template))

(defn system-dep-graph
  [ns-name {:keys [db]}]
  (let [template (-> ["(defn new-%1s-system"                                                                 always
                      "  \"Constructs the component system for the application.\""                           always
                      "  []"                                                                                 always
                      "  (let [metrics-registry (new-registry)]"                                             always
                      "    (jvm/instrument-jvm metrics-registry)"                                            always
                      "    (map->Quotations-Web-System"                                                      always
                      "     {:web-server       (component/using (new-web-server) [:metrics-registry])"       always
                      "      :mongo-connection (new-mongo-connection)"                                       #(mongodb? db)
                      "      :metrics-reporter (component/using (new-metrics-reporter) [:metrics-registry])" always
                      "      :metrics-registry  metrics-registry})))"                                        always]
                     construct-template)]
    (format template ns-name)))

(defn template-data
  [name options]
  (let [ns-name (sanitize-ns name)]
    (merge {:name name
            :ns-name ns-name
            :sanitized (name-to-path name)
            :docker-name (string/replace name #"-" "")
            :dockerized-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :person-template "{{person}}"
            :location-template "{{location}}"
            :healthchecks-template-open "{{#healthchecks}}"
            :healthchecks-template-close "{{/healthchecks}}"
            :healthcheck-name-template "{{name}}"
            :healthcheck-status-template "{{status}}"
            :title-template "{{title}}"
            :content-template "{{{content}}}"
            :header-template "{{>header}}"
            :footer-template "{{>footer}}"
            :system-ns (system-ns-str ns-name options)
            :system-comp-list (system-comp-list-str options)
            :system-dep-graph (system-dep-graph ns-name options)}
           (when (:db options) (mongodb ns-name)))))

(defn create-project
  [name files-fn options]
  (let [data (template-data name options)
        files (files-fn data options)]
     (apply ->files data files)))

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-d" "--db DATABASE" "Database to be used. Currently only supports `mongodb`"
    :parse-fn keyword
;    :default :none
    :validate [#(= % :mongodb) "Currently only mongodb is currently supported"]]
   ["-H" "--hostname HOST" "Remote host"
    ;; Specify a string to output in the default column in the options summary
    ;; if the default value's string representation is very ugly
    :default-desc "localhost"]
   ;; If no required argument description is given, the option is assumed to
   ;; be a boolean option defaulting to nil
   [nil "--detach" "Detach from controlling process"]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use assoc-fn to create non-idempotent options
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> [""
        "A Leiningen template which produces a docker ready site or api with embedded"
        "Jetty web-server, Graphite/Grafanna instrumentation and many customisations."
        ""
        "Usage: lein new dockerised-web-app <project-name> <type> [options]"
        ""
        "Types:"
        "  api      Start a new server"
        "  site     Stop an existing server"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn dockerised-web-app
  ([name] (dockerised-web-app name "--help"))
  ([name & args]
   (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
     (cond
       (:help options) (exit 0 (usage summary))
       (not= (count arguments) 1) (exit 1 (usage summary))
       errors (exit 1 (error-msg errors)))
   
     ;; Execute program with options
     (case (first arguments)
       "api" (create-project name api-files options)
       "site" (create-project name site-files options)
       (exit 1 (usage summary))))))
