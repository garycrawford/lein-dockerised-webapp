(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [reloaded.repl :refer [system init start stop go reset]]
    [{{ns-name}}.system :refer [new-{{ns-name}}-system]]))

(reloaded.repl/set-init! new-{{ns-name}}-system)
