(ns nightcode.git-test
  (:require [clojure.test :refer [deftest is testing]]
            [nightcode.git :as git]))

;; --- address->name ---

(deftest test-address->name
  (testing "Extracts the repo name from a .git URL"
    (is (= "Clojure-code" (git/address->name "https://github.com/juv4uk/Clojure-code.git")))
    (is (= "Clojure-code" (git/address->name "git@github.com:juv4uk/Clojure-code.git"))))
  (testing "Returns nil for URLs that don't end in .git"
    (is (nil? (git/address->name "https://github.com/juv4uk/Clojure-code")))))
