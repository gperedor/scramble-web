(ns backend.scramble-test
  (:require [clojure.test :refer :all]
            [backend.scramble :refer :all]))

(deftest empty-cases
  (testing "left empty doesn't crash"
    (is (not (scramble? "" "hello"))))
  (testing "right empty is always true"
    (is (scramble? "hello" ""))
    (is (scramble? "" ""))))

(deftest nil-punned
  (testing "left nil is as in empty case"
    (is (not (scramble? nil "hello"))))
  (testing "right nil is as in empty case"
    (is (scramble? "hello" nil)))
  (testing "both-nil is as in empty cases"
    (is (scramble? nil nil))))

(deftest one-to-one
  (testing "true when you can map chars one-to-one from right to left"
    (is (scramble? "dlowrxyz" "world"))))

(deftest more-occurrences
  (testing "true when chars in right appear more times in left"
    (is (scramble? "ddlowwwwwrxyz" "world"))))

(deftest fewer-occurrences
  (testing "false when chars in right appear fewer time in left"
    (is (not (scramble? "helo" "hello")))))
