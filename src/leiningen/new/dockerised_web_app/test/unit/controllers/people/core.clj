(ns {{ns-name}}.unit.controllers.people.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.people.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query]]))

(defn result-checker
  [value]
  (fn [result] (-> result
                   (get-in [:body :result])
                   (= value))))

(facts "when listing people"
  (fact "an empty result"
    (list-people {:mongodb ..mongodb..}) => (result-checker []) 
    (provided
      (find-by-query ..mongodb.. "people" {}) => []))
  
  (fact "an single result"
    (list-people {:mongodb ..mongodb..}) => (result-checker [{:id "id" :name "name" :location "location"}])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [{:id "id" :name "name" :location "location"}]))

  (fact "additional fields aren't returned"
    (list-people {:mongodb ..mongodb..}) => (result-checker [{:id "id"}])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [{:id "id" :something :else :another :thing}])))
