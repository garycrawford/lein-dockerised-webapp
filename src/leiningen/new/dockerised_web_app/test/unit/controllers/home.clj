(ns {{ns-name}}.unit.controllers.home
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home :refer :all]))

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (index) => (status? 200))

  (fact "the response has a text/html content type"
    (index) => (content-type? "text/html"))

  (fact "the response model is well formed"
    (let [response (index)]
      (get-in response [:body :model])) => {:person   "Anonomous User"
                                            :location "Timbuktu"})

  (fact "the correct view is returned"
    (let [response (index)]
      (get-in response [:body :view :path])) => "templates/home/about.mustache")

  (fact "a view function is returned"
    (let [response (index)]
      (get-in response [:body :view :fn])) => fn?))
