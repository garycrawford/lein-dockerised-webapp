(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "dockerised-web-app"))

(defn src-files
  [data] 
  [["src/{{sanitized}}/zygote.clj" (render "src/zygote.clj" data)]
   ["src/{{sanitized}}/web_server.clj" (render "src/api_web_server.clj" data)]
   ["src/{{sanitized}}/system.clj" (render "src/system.clj" data)]
   ["src/{{sanitized}}/metrics_reporter.clj" (render "src/metrics_reporter.clj" data)]
   ["src/{{sanitized}}/logging_config.clj" (render "src/logging_config.clj" data)]])

(defn test-files
  [data]
  [["test/{{sanitized}}/test/metrics_reporter.clj" (render "test/metrics_reporter.clj" data)]])

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

(defn api-files
  [data]
  (concat (src-files data)
          (test-files data)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)))
