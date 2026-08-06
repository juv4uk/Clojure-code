(ns nightcode.utils-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [nightcode.utils :as u]
            [clojure.java.io :as io]))

;; --- get-extension ---

(deftest test-get-extension
  (testing "Returns correct extension for common file types"
    (is (= "clj"  (u/get-extension "hello.clj")))
    (is (= "java" (u/get-extension "Main.java")))
    (is (= "edn"  (u/get-extension "config.edn")))
    (is (= "cljs" (u/get-extension "app.cljs"))))
  (testing "Returns lowercase extension"
    (is (= "clj" (u/get-extension "Hello.CLJ"))))
  (testing "Handles multiple dots correctly"
    (is (= "jar" (u/get-extension "clojure-code-1.0.0-standalone.jar")))))

;; --- sanitize-name ---

(deftest test-sanitize-name
  (testing "Basic sanitization"
    (is (= "my-project" (u/sanitize-name "My Project")))
    (is (= "hello-world" (u/sanitize-name "Hello, World!")))
    (is (= "clojure-app" (u/sanitize-name "  Clojure App  "))))
  (testing "Removes apostrophes"
    (is (= "dont-panic" (u/sanitize-name "Don't Panic"))))
  (testing "Handles numbers"
    (is (= "project-2" (u/sanitize-name "Project 2")))))

;; --- normalize-text-size ---

(deftest test-normalize-text-size
  (testing "Stays within bounds 12–24"
    (is (= 12 (u/normalize-text-size 1)))
    (is (= 12 (u/normalize-text-size 12)))
    (is (= 24 (u/normalize-text-size 100)))
    (is (= 24 (u/normalize-text-size 24))))
  (testing "Always returns even number"
    (is (even? (u/normalize-text-size 13)))
    (is (even? (u/normalize-text-size 15)))
    (is (even? (u/normalize-text-size 17))))
  (testing "Normal values"
    (is (= 16 (u/normalize-text-size 16)))
    (is (= 18 (u/normalize-text-size 18)))))

;; --- parent-path? ---

(deftest test-parent-path?
  (testing "Same path returns true"
    (is (true? (u/parent-path? "/a/b" "/a/b"))))
  (testing "Nil child returns false"
    (is (false? (u/parent-path? "/a/b" nil))))
  (testing "Non-parent path returns false"
    (is (false? (u/parent-path? "/a/b" "/a/c/d")))))

;; --- build-systems ---

(deftest test-build-systems
  (testing "Detects lein project from project.clj"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-lein-proj")]
      (.mkdirs tmp)
      (.createNewFile (io/file tmp "project.clj"))
      (try
        (is (contains? (u/build-systems (.getCanonicalPath tmp)) :lein))
        (finally
          (doseq [f (.listFiles tmp)] (.delete f))
          (.delete tmp)))))
  (testing "Does NOT detect boot projects anymore"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-boot-proj")]
      (.mkdirs tmp)
      (.createNewFile (io/file tmp "build.boot"))
      (try
        (is (not (contains? (u/build-systems (.getCanonicalPath tmp)) :boot)))
        (finally
          (doseq [f (.listFiles tmp)] (.delete f))
          (.delete tmp)))))
  (testing "Returns empty set for unknown project"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-empty-proj")]
      (.mkdirs tmp)
      (try
        (is (empty? (u/build-systems (.getCanonicalPath tmp))))
        (finally (.delete tmp))))))
