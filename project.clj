(defproject dockerised-web-app/lein-template "0.1.1"
  :description "Lein template for generating a dockerised Clojure web-app"
  :url "https://github.com/garycrawford/lein-dockerised-webapp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[camel-snake-kebab "0.3.1" :exclusions [org.clojure/clojure]]]
  :scm {:name "git"
        :url "https://github.com/garycrawford/lein-dockerised-webapp"})
