(ns {{ns-name}}.components.mongodb.core
  (:require [monger.collection :as mc]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]))

(defn find-one
  [{:keys [db]} collection query]
  (mc/find-one-as-map db collection query))

(defn insert
  [{:keys [db]} collection document]
  (mc/insert db collection document))
