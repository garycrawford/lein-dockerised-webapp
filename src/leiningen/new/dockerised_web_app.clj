(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]]))

(def render (renderer "dockerised-web-app"))

(defn dockerised-web-app
  [name]
  (let [data {:name name
              :ns-name (sanitize-ns name)
              :sanitized (name-to-path name)}]
    (->files data ["project.clj" (render "project.clj" data)]
                  ["Dockerfile" (render "Dockerfile" data)]
                  [".dockerignore" (render "dockerignore" data)]
                  ["dev/dev.clj" (render "dev.clj" data)]
                  ["dev/user.clj" (render "user.clj" data)]
                  ["src/{{sanitized}}/zygote.clj" (render "zygote.clj" data)]
                  ["src/{{sanitized}}/web_server.clj" (render "web_server.clj" data)]
                  ["src/{{sanitized}}/system.clj" (render "system.clj" data)]
                  ["src/{{sanitized}}/metrics.clj" (render "metrics.clj" data)]
                  ["config/dev.env" (render "dev.env" data)]
                  ["resources/routes.txt" (render "routes.txt")])))
