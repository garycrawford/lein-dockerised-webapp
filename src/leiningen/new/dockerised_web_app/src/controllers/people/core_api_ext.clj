(ns {{ns-name}}.controllers.people.core
  (:require [{{ns-name}}.responses :refer [json-ok]]
            [{{ns-name}}.components.mongodb.core :as m]
            [ring.util.response :refer [not-found created status header]]))

(def collection "people")

(defn whitelist
  [person]
  (select-keys person [:name :location :id]))

(defn list-people
  [{:keys [mongodb]}]
  (let [people (m/find-by-query mongodb collection {})]
    (json-ok {:result (map whitelist people)})))

(defn create-person
  [{:keys [mongodb]} {:keys [name location]}]
  (let [{:keys [id]} (m/insert mongodb collection {:name name :location location})]
    (-> (created (str "/api/people/" id))
        (header "Content-Type" "application/json"))))

(defn read-person
  [{:keys [mongodb]} id]
  (if-let [person (m/find-by-id mongodb collection id)]
    (json-ok {:result (whitelist person)})
    (not-found {})))

(defn update-person
  [{:keys [mongodb]} {:keys [id name location]}]
  (m/update mongodb collection {:name name :location location :id id})
  (-> (status {} 204)
      (header "Content-Type" "application/json")
      (header "Location" (str "/api/people/" id))))

(defn delete-person
  [{:keys [mongodb]} id]
  (m/delete mongodb collection id)
  (-> (status {} 204)
      (header "Content-Type" "application/json")))
