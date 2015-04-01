(ns {{ns-name}}.integration.controllers.home
  (:require [midje.sweet :refer [fact facts]]
            [kerodon.core :refer [session visit]]
            [kerodon.test :refer [has status?]]
            [metrics.core :refer [new-registry]]
            [{{ns-name}}.web-server :refer [create-handler]]))

(def app (create-handler (new-registry)))

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (-> app
        session
        (visit "/")
        (has (status? 200)))))
