(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [reloaded.repl :refer [system init start stop go reset]]
    [cemerick.pomegranate :refer [add-dependencies]]
    [{{ns-name}}.system :refer [new-{{ns-name}}-system]]))

(reloaded.repl/set-init! new-{{ns-name}}-system)

(defn add-dependency
  "Allows dynamic adding of dependencies to the classpath."
  [dependency version]
  (add-dependencies :coordinates  [[dependency version]]
                    :repositories {"clojars" "http://clojars.org/repo"
                                   "central" "http://repo1.maven.org/maven2/"}))
