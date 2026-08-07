(ns nightcode.lein-test
  (:require [clojure.test :refer [deftest is testing]]
            [nightcode.lein :as lein]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]))

;; --- get-project-clj-path ---

(deftest test-get-project-clj-path
  (testing "Appends project.clj to the given directory"
    (is (= (.getCanonicalPath (io/file "." "project.clj"))
           (lein/get-project-clj-path ".")))
    (is (str/ends-with? (lein/get-project-clj-path (System/getProperty "java.io.tmpdir"))
          (str File/separator "project.clj")))))
