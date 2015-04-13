(ns {{ns-name}}.unit.controllers.people.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.people.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query find-by-id insert]]))

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

(facts "when creating a person"
  (fact "success result will have a 201 status code"
    (create-person {:mongodb ..mongodb..} {:name "name" :location "location"}) => (contains {:status 201})
    (provided
      (insert ..mongodb.. "people" {:name "name" :location "location"}) => {:id "id"}))
  
  (fact "success result will have a location and content type headers"
    (create-person {:mongodb ..mongodb..} {:name "name" :location "location"}) => (contains {:headers {"Location" "/api/people/id"
                                                                                                       "Content-Type" "application/json"}})
    (provided
      (insert ..mongodb.. "people" {:name "name" :location "location"}) => {:id "id"})))

(facts "when creating a person"
  (fact "success will result in a 200 status code"
    (read-person {:mongodb ..mongodb..} ..id..) => (contains {:status 200})
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => {:id "id" :name "name" :location "location"}))
  
  (fact "success will result in correct data"
    (read-person {:mongodb ..mongodb..} ..id..) => (result-checker {:id "id" :name "name" :location "location"})
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => {:id "id" :name "name" :location "location"})))
