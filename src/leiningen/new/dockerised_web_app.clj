(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]))

(def render (renderer "dockerised-web-app"))

(defn construct-template
  [lines]
  (->> lines
       (partition 2)
       (map (fn [[line check-fn]] (when (check-fn) line)))
       (filter (complement nil?))
       (string/join \newline)))

(def always (constantly true))
(def mongodb? (partial = :mongodb))

(defn dev-profile
  [ns-name dockerised-svr {:keys [db]}]
  (let [template (-> ["{:dev {:source-paths [\"dev\"]"                                                                            always
                      "       :plugins [[lein-ancient \"0.6.3\"]"                                                                 always
                      "                 [jonase/eastwood \"0.2.1\"]"                                                              always
                      "                 [lein-bikeshed \"0.2.0\"]"                                                                always
                      "                 [lein-kibit \"0.0.8\"]"                                                                   always
                      "                 [lein-environ \"1.0.0\"]"                                                                 always
                      "                 [lein-midje \"3.1.3\"]]"                                                                  always
                      "       :dependencies [[org.clojure/tools.namespace \"0.2.10\"]"                                            always
                      "                      [slamhound \"1.5.5\"]"                                                               always
                      "                      [com.cemerick/pomegranate \"0.3.0\" :exclusions [org.codehaus.plexus/plexus-utils]]" always
                      "                      [prone \"0.8.1\"]"                                                                   always
                      "                      [midje \"1.6.3\"]"                                                                   always
                      "                      [org.clojure/test.check \"0.7.0\"]"                                                  always
                      "                      [com.gfredericks/test.chuck \"0.1.16\"]"                                             always
                      "                      [kerodon \"0.5.0\"]]"                                                                always
                      "       :env {:metrics-host \"192.168.59.103\""                                                             always
                      "             :metrics-port 2003"                                                                           always
                      "             :mongodb-uri  \"mongodb://192.168.59.103/%1$s\""                                              #(mongodb? db)
                      "             :app-name     \"%1$s\""                                                                       always
                      "             :hostname     \"%2$s\"}"                                                                      always
                      "       :ring {:stacktrace-middleware prone.middleware/wrap-exceptions}}}"                                  always]
                     construct-template)]
    (format template ns-name dockerised-svr)))

(defn docker-compose
  [docker-name svr-name ns-name {:keys [db]}]
  (let [template (-> ["%1s:"                                                     always
                      "  build: ."                                               always
                      "  ports:"                                                 always
                      "   - \"1234:1234\""                                       always
                      "   - \"21212:21212\""                                     always
                      "  volumes:"                                               always
                      "   - .:/usr/src/app"                                      always
                      "  links:"                                                 always
                      "   - metrics"                                             always
                      "   - persistence"                                         #(mongodb? db)
                      "  hostname: \"%2s\""                                      always
                      "  environment:"                                           always
                      "     MONGODB_URI: mongodb://192.168.59.103/%3$s"          #(mongodb? db)
                      "     METRICS_HOST: 192.168.59.103"                        always
                      "     METRICS_PORT: 2003"                                  always
                      "     APP_NAME: %3$s"                                      always
                      "  command: lein repl :headless :host 0.0.0.0 :port 21212" always
                      "metrics:"                                                 always
                      "  image: garycrawford/grafana_graphite:0.0.1"             always
                      "  volumes:"                                               always
                      "   - ./dashboards:/src/dashboards"                        always
                      "  ports:"                                                 always
                      "   - \"80:80\""                                           always
                      "   - \"2003:2003\""                                       always
                      "persistence:"                                             #(mongodb? db)
                      "   image: mongo:3.0.1"                                    #(mongodb? db)
                      "   ports:"                                                #(mongodb? db)
                      "   - \"27017:27017\""                                     #(mongodb? db)]
                     construct-template)]
    (format template docker-name svr-name ns-name)))

(defn project-deps
  [{:keys [db]}]
  (-> [" :dependencies [[org.clojure/clojure \"1.6.0\"]"                                        always
       "                [ring/ring-jetty-adapter \"1.3.2\"]"                                    always
       "                [ring/ring-json \"0.3.1\"]"                                             always
       "                [ring/ring-defaults \"0.1.4\"]"                                         always
       "                [scenic \"0.2.3\" :exclusions [org.clojure/tools.reader]]"              always
       "                [reloaded.repl \"0.1.0\"]"                                              always
       "                [com.stuartsierra/component \"0.2.3\"]"                                 always
       "                [metrics-clojure \"2.5.0\"]"                                            always
       "                [metrics-clojure-jvm \"2.5.0\"]"                                        always
       "                [metrics-clojure-graphite \"2.5.0\"]"                                   always 
       "                [metrics-clojure-ring \"2.5.0\"]"                                       always
       "                [environ \"1.0.0\"]"                                                    always
       "                [com.taoensso/timbre \"3.4.0\" :exclusions [org.clojure/tools.reader]]" always
       "                [prismatic/schema \"0.4.0\"]"                                           always
       "                [robert/hooke \"1.3.0\"]"                                               always
       "                [com.novemberain/monger \"2.0.1\"]"                                     #(mongodb? db)
       "                [de.ubercode.clostache/clostache \"1.4.0\"]]"                           always]
      construct-template))

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

(defn healthcheck-list-template
  []
  (->> ["<ul>"
        "{{#healthchecks}}"
        "  <li>{{name}}: {{status}}</li>"
        "{{/healthchecks}}"
        "</ul>"]
       (string/join \newline)))

(defn page-template
  []
  (->> ["{{>header}}"
        "  <div class=\"default\">"
        "    {{{content}}}"
        "  </div>"
        "{{>footer}}"]
       (string/join \newline)))

(defn template-data
  [name options]
  (let [ns-name (sanitize-ns name)
        docker-name (string/replace name #"-" "")
        dockerised-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")]
    {:name name
     :ns-name ns-name
     :sanitized (name-to-path name)
     :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
     :person-template "{{person}}"
     :location-template "{{location}}"
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

(template-data "jimmy" {:db :mongodb})

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
