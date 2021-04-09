(ns frontend.core-test
  (:require
   [reagent.core :as r]
   [cljs.test :refer-macros [deftest is testing async]]
   [cljs-http.client :as http]
   [frontend.core :refer [remote-scramble?]]
   [cljs.core.async :as async :refer [go <!]]))

(defn mock-response
  [response]
  (go response))

(deftest get-scrambled-side-effects
  (testing "when it's a scramble"
    (async done
           (go
             (let [str1 (r/atom "dummy")
                   str2 (r/atom "dummy")
                   result-text (r/atom "test")
                   result-style (r/atom {:color "test"})
                   resp-success
                   (with-redefs [http/get #(mock-response
                                            {:status 200,
                                             :body {:scrambled true}})]
                     (<! (remote-scramble? str1 str2 result-text result-style)))]
               (is (and resp-success (=  "A scramble!" @result-text)))
               (is (and resp-success (= {:color "green"} @result-style)))
               (done))))))

(deftest get-not-scrambled-side-effects
  (testing "when it's not a scramble"
    (async done
           (go
             (let [str1 (r/atom "dummy")
                   str2 (r/atom "dummy")
                   result-text (r/atom "test")
                   result-style (r/atom {:color "test"})
                   resp-success
                   (with-redefs [http/get #(mock-response {:status 200
                                                           :body {:scrambled false}})]
                     (<! (remote-scramble? str1 str2 result-text result-style)))]
               (is (= [true "Not a scramble"] [resp-success  @result-text]))
               (is (= [true {:color "purple"}] [resp-success @result-style]))
               (done))))))

(deftest get-failed
  (testing "when the request fails"
    (async done
           (go
             (let [str1 (r/atom "dummy")
                   str2 (r/atom "dummy")
                   result-text (r/atom "test")
                   result-style (r/atom {:color "test"})
                   resp-success
                   (with-redefs [http/get #(mock-response {:status 400})]
                     (<! (remote-scramble? str1 str2 result-text result-style)))]
               (is (= [false "An error occurred"] [resp-success  @result-text]))
               (is (= [false {:color "red"}] [resp-success @result-style]))
               (done))))))
