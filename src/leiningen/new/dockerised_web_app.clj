(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]))

(def render (renderer "dockerised-web-app"))

(defn usage  []
    (println "Usage: lein new stevedore <project-name> <web | api> [ <arg>... ]"))

(defn src-files
  [data] 
  [["src/{{sanitized}}/zygote.clj" (render "src/zygote.clj" data)]
   ["src/{{sanitized}}/web_server.clj" (render "src/web_server.clj" data)]
   ["src/{{sanitized}}/system.clj" (render "src/system.clj" data)]
   ["src/{{sanitized}}/graphite_reporter.clj" (render "src/graphite_reporter.clj" data)]
   ["src/{{sanitized}}/logging.clj" (render "src/logging.clj" data)]])

(defn test-files
  [data]
  [["test/{{sanitized}}/test/graphite_reporter.clj" (render "test/graphite_reporter.clj" data)]])

(defn dashboards-files
  [data]
  [["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
   ["dashboards/app-stats.json" (render "dashboards/app-stats.json" data)]])

(defn resources-files
  [data]
  [["resources/routes.txt" (render "resources/routes.txt")]])

(defn dev-files
  [data]
  [["dev/user.clj" (render "dev/user.clj" data)]])

(defn project-files
  [data]
   [["project.clj" (render "project.clj" data)]
    ["profiles.clj" (render "profiles.clj" data)]
    ["Dockerfile" (render "Dockerfile" data)]
    ["docker-compose.yml" (render "docker-compose.yml" data)]
    [".dockerignore" (render "dockerignore" data)]
    [".gitignore" (render "gitignore" data)]
    ["README.md" (render "README.md" data)]])

(defn all-files
  [data]
  (concat (src-files data)
          (test-files data)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)))

(defn tamplate-data
  [name]
  {:name name
   :ns-name (sanitize-ns name)
   :sanitized (name-to-path name)
   :docker-name (clojure.string/replace name #"-" "")
   :dockerized-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")
   :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))})

(defn dockerised-web-app
  ([name]
   (usage))
  ([name template & args]
   (println template)
   (println args)
   (let [data (tamplate-data name) 
         files (all-files data)]
     (apply ->files data files))))
