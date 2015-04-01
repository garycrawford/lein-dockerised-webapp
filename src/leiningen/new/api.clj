(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "dockerised-web-app"))

(defn controllers-files
  [data]
  [["src/{{sanitized}}/controllers/home.clj" (render "src/controllers/home_api.clj" data)]
   ["src/{{sanitized}}/controllers/healthcheck.clj" (render "src/controllers/healthcheck.clj" data)]])

(defn models-files
  [data]
  [["src/{{sanitized}}/models/home.clj" (render "src/models/home.clj" data)]
   ["src/{{sanitized}}/models/healthcheck.clj" (render "src/models/healthcheck.clj" data)]])

(defn views-files
  [data]
  [["src/{{sanitized}}/views/healthcheck.clj" (render "src/views/healthcheck.clj" data)]
   ["src/{{sanitized}}/views/shared.clj" (render "src/views/shared.clj" data)]])

(defn templates-files
  [data]
  [["resources/templates/shared/default.mustache" (render "resources/templates/shared/default.mustache" data)]
   ["resources/templates/shared/header.mustache" (render "resources/templates/shared/header.mustache" data)]
   ["resources/templates/shared/footer.mustache" (render "resources/templates/shared/footer.mustache" data)]
   ["resources/templates/healthcheck/healthcheck-list.mustache" (render "resources/templates/healthcheck/healthcheck-list.mustache" data)]])

(defn src-files
  [data] 
  [["src/{{sanitized}}/zygote.clj" (render "src/zygote.clj" data)]
   ["src/{{sanitized}}/web_server.clj" (render "src/web_server_api.clj" data)]
   ["src/{{sanitized}}/system.clj" (render "src/system.clj" data)]
   ["src/{{sanitized}}/metrics_reporter.clj" (render "src/metrics_reporter.clj" data)]
   ["src/{{sanitized}}/logging_config.clj" (render "src/logging_config.clj" data)]])

(defn public-files
  [data]
  [["resources/public/css/styles.css" (render "resources/public/css/styles.css" data)]])

(defn test-files
  [data]
  [["test/{{sanitized}}/unit/metrics_reporter.clj" (render "test/unit/metrics_reporter.clj" data)]])

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
          (project-files data)
          (controllers-files data)
          (views-files data)
          (models-files data)
          (templates-files data)
          (public-files data)))
