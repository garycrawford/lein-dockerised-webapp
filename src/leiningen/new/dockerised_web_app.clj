(ns leiningen.new.dockerised-web-app
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]))

(def render (renderer "dockerised-web-app"))

(defn usage  []
  (println)
  (println "Usage: lein new stevedore <project-name> (site | api) [ <arg>... ]")
  (println))

(defn template-data
  [name]
  {:name name
   :ns-name (sanitize-ns name)
   :sanitized (name-to-path name)
   :docker-name (clojure.string/replace name #"-" "")
   :dockerized-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")
   :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
   :person-template "{{person}}"
   :location-template "{{location}}"
   :healthchecks-template-open "{{#healthchecks}}"
   :healthchecks-template-close "{{/healthchecks}}"
   :healthcheck-name-template "{{name}}"
   :healthcheck-status-template "{{status}}"})

(defn create-project
  [name files-fn]
  (let [data (template-data name)
        files (files-fn data)]
     (apply ->files data files)))

(defn unknown-template-feedback
  [template]
  (println)
  (println
    (format "sorry, I don't recognise '%s' as a template option. Try 'site' or 'api'"
            template))
  (usage))

(defn new-project
  [name template args]
  (cond
    (= template "site") (create-project name site-files)
    (= template "api")  (create-project name api-files)
    :else (unknown-template-feedback template)))

(defn dockerised-web-app
  ([name]
   (usage))
  ([name template & args]
   (new-project name template args)))
