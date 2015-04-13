(ns {{ns-name}}.unit.components.mongodb.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.components.mongodb.core :refer :all]))

(def mongo-id (monger.conversion/to-object-id "5527c7c1e4b0ef1647b88d9f"))
(def external-id "EvZ7vRrEyPUgJ3mWqVmm")
(def dummy-doc {:_id mongo-id})
(def dummy-query {:id external-id})

(fact "to support obscuring mongodb id's from the outside world"
  (fact "the mongodb id can be encrypted"
      (mongoid->external mongo-id) => external-id)

  (fact "an encrypted mongodb id can be decrypted"
      (external->mongoid external-id) => mongo-id)
  
  (fact "mongodb docs have _id removed and id appended"
      (externalise dummy-doc) => {:id external-id})

  (fact "externalise can handle nil query results"
      (externalise nil) => nil)

  (fact "incoming queries will have :id replaced with :_id"
      (marshall-query dummy-query) => (contains {:_id mongo-id})))

(fact "to support saving all historical states of data"

  (fact "incoming queries will be changed to filter deleted docs")
      (marshall-query dummy-query) => (contains {:current.deleted {"$exists" false}})  

  (fact "incoming queries will be changed to filter deleted docs")
      (marshall-query dummy-query) => (contains {:current.deleted {"$exists" false}}))
