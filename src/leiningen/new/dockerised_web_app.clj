(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [leiningen.new.common-templates :refer :all]))

(def render (renderer "dockerised-web-app"))

(defn template-data
  [name options]
  (let [ns-name (sanitize-ns name)
        docker-name (string/replace name #"-" "")
        dockerised-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")]
    {:name name
     :ns-name ns-name
     :sanitized (name-to-path name)
     :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
     :name-template "{{name}}"
     :location-template "{{location}}"
     :anti-forgery-field "{{{anti-forgery-field}}}"
     :healthcheck-list-template (healthcheck-list-template)
     :title-template "{{title}}"
     :page-template (page-template)
     :system-ns (system-ns-str ns-name options)
     :system-comp-list (system-comp-list-str options)
     :system-dep-graph (system-dep-graph ns-name options)
     :project-deps (project-deps options)
     :dockerised-svr dockerised-svr
     :docker-compose (docker-compose docker-name dockerised-svr ns-name options)
     :dev-profile (dev-profile ns-name dockerised-svr options)}))

(defn create-project
  [name files-fn options]
  (let [data (template-data name options)
        files (files-fn data options)]
     (apply ->files data files)))

(def cli-options
  [["-d" "--db DATABASE" "Database to be used. Currently only supports `mongodb`"
    :parse-fn keyword
    :validate [#(= % :mongodb) "Currently only mongodb is currently supported"]]
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
        "  api      Create a new web api"
        "  site     Create a new site"
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
