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
                  ["profiles.clj" (render "profiles.clj" data)]
                  ["Dockerfile" (render "Dockerfile" data)]
                  ["docker-compose.yml" (render "docker-compose.yml" data)]
                  [".dockerignore" (render "dockerignore" data)]
                  [".gitignore" (render "gitignore" data)]
                  ["README.md" (render "README.md" data)]
                  ["dev/user.clj" (render "dev/user.clj" data)]
                  ["src/{{sanitized}}/zygote.clj" (render "src/zygote.clj" data)]
                  ["src/{{sanitized}}/web_server.clj" (render "src/web_server.clj" data)]
                  ["src/{{sanitized}}/system.clj" (render "src/system.clj" data)]
                  ["src/{{sanitized}}/graphite_reporter.clj" (render "src/graphite_reporter.clj" data)]
                  ["test/{{sanitized}}/test/graphite_reporter.clj" (render "test/graphite_reporter.clj" data)]
                  ["src/{{sanitized}}/logging.clj" (render "src/logging.clj" data)]
                  ["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
                  ["dashboards/app-stats.json" (render "dashboards/app-stats.json" data)]
                  ["resources/routes.txt" (render "resources/routes.txt")])))
