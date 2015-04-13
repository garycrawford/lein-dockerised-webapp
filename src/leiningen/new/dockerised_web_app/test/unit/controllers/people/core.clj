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
  (fact "a result can be an empty array"
    (list-people {:mongodb ..mongodb..}) => (result-checker []) 
    (provided
      (find-by-query ..mongodb.. "people" {}) => []))
  
  (fact "a result can be a non-empty array"
    (list-people {:mongodb ..mongodb..}) => (result-checker [{:id "id" :name "name" :location "location"}])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [{:id "id" :name "name" :location "location"}]))

  (fact "only id, name and location fields are returned"
    (list-people {:mongodb ..mongodb..}) => (result-checker [{:id "id" :name "name" :location "location"}])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [{:id "id" :name "name" :location "location" :something :else :another :thing}])))

(facts "when creating a person"
  (fact "success result will have a 201 status code"
    (create-person {:mongodb ..mongodb..} {:name "name" :location "location"}) => (contains {:status 201})
    (provided
      (insert ..mongodb.. "people" {:name "name" :location "location"}) => {:id "id"}))
  
  (fact "success result will have a populated location header"
    (create-person {:mongodb ..mongodb..} {:name "name" :location "location"}) => (contains {:headers (contains {"Location" "/api/people/id"})})
    (provided
      (insert ..mongodb.. "people" {:name "name" :location "location"}) => {:id "id"}))
  
  (fact "success result will have an application/json content type header"
    (create-person {:mongodb ..mongodb..} {:name "name" :location "location"}) => (contains {:headers (contains {"Content-Type" "application/json"})})
    (provided
      (insert ..mongodb.. "people" {:name "name" :location "location"}) => {:id "id"})))

(facts "when reading a person"
  (fact "success will result in a 200 status code"
    (read-person {:mongodb ..mongodb..} ..id..) => (contains {:status 200})
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => {:id "id" :name "name" :location "location"}))
  
  (fact "success will result in correct data"
    (read-person {:mongodb ..mongodb..} ..id..) => (result-checker {:id "id" :name "name" :location "location"})
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => {:id "id" :name "name" :location "location"})))
