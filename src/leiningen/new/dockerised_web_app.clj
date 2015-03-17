(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]))

(def render (renderer "dockerised-web-app"))

(defn dockerised-web-app
  [name]
  (let [data {:name name
              :ns-name (sanitize-ns name)
              :sanitized (name-to-path name)
              :docker-name (clojure.string/replace name #"-" "")
              :dockerized-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")}]
    (->files data ["project.clj" (render "project.clj" data)]
                  ["Dockerfile" (render "Dockerfile" data)]
                  [".dockerignore" (render "dockerignore" data)]
                  ["docker-compose.yml" (render "docker-compose.yml" data)]
                  ["dev/dev.clj" (render "dev.clj" data)]
                  ["dev/user.clj" (render "user.clj" data)]
                  ["src/{{sanitized}}/zygote.clj" (render "zygote.clj" data)]
                  ["src/{{sanitized}}/web_server.clj" (render "web_server.clj" data)]
                  ["src/{{sanitized}}/system.clj" (render "system.clj" data)]
                  ["src/{{sanitized}}/graphite_reporter.clj" (render "graphite_reporter.clj" data)]
                  ["src/{{sanitized}}/logging.clj" (render "logging.clj" data)]
                  ["dashboards/dashboard-loader.js" (render "dashboard-loader.js" data)]
                  ["dashboards/app-stats.json" (render "app-stats.json" data)]
                  ["config/dev.env" (render "dev.env" data)]
                  ["resources/routes.txt" (render "routes.txt")])))
