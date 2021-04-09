(ns backend.server-test
  (:require [clojure.test :refer :all]
            [backend.server :refer :all]
            [ring.mock.request :as mock]))

(deftest well-formed-requests
  (is (= (-> (mock/request :get "/scramble" {:str1 "hellow", :str2 "olleh"})
           app :body slurp)
         "{\"scrambled\":true}"))
  (is (= (-> (mock/request :get "/scramble" {:str1 "hellw", :str2 "olleh"})))
      "{\"scrambled\":false}"))

(deftest malformed-request
  (is (= (-> (mock/request :get "/scramble")
           app :status)
         400)))

(deftest not-found
  (is (= (-> (mock/request :get "/eh")
             app :status))
      404))
