(ns nightcode.utils-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [nightcode.utils :as u]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]))

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
  (testing "Stays within bounds 12-24"
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

;; --- remove-returns ---

(deftest test-remove-returns
  (testing "Strips carriage returns"
    (is (= "ab" (u/remove-returns "a\rb"))))
  (testing "Strips ANSI escape sequences up to the next newline"
    (is (= "hello\ngoodbye" (u/remove-returns (str "hello" (char 27) "[31mworld\ngoodbye")))))
  (testing "Leaves plain text untouched"
    (is (= "hello\nworld" (u/remove-returns "hello\nworld")))))

;; --- get-relative-path ---

(deftest test-get-relative-path
  (testing "Returns a path relative to the project root"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-relative-proj")
          src (io/file tmp "src" "core.clj")]
      (.mkdirs (.getParentFile src))
      (.createNewFile src)
      (try
        (is (= (str "src" File/separator "core.clj")
               (u/get-relative-path (.getCanonicalPath tmp) (.getCanonicalPath src))))
        (finally
          (.delete src)
          (.delete (.getParentFile src))
          (.delete tmp))))))

;; --- get-project-path ---

(deftest test-get-project-path
  (testing "Finds the containing project for a nested file"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-project-path")
          nested (io/file tmp "src" "core.clj")]
      (.mkdirs (.getParentFile nested))
      (.createNewFile (io/file tmp "project.clj"))
      (.createNewFile nested)
      (try
        (is (= (.getCanonicalPath tmp)
               (u/get-project-path (.getCanonicalPath nested) {:project-set #{}})))
        (finally
          (.delete nested)
          (.delete (.getParentFile nested))
          (.delete (io/file tmp "project.clj"))
          (.delete tmp)))))
  (testing "Returns the path itself when it's a known project in project-set"
    (let [tmp (io/file (System/getProperty "java.io.tmpdir") "test-project-set")]
      (.mkdirs tmp)
      (try
        (let [path (.getCanonicalPath tmp)]
          (is (= path (u/get-project-path path {:project-set #{path}}))))
        (finally (.delete tmp))))))

;; --- translation completeness ---
;; Every translatable control must have both :en and :uk text, or the
;; language toggle silently falls back to English for the missing entry.

(defn- all-translated? [translations]
  (every? (fn [[_ strs]]
            (and (not (str/blank? (:en strs)))
                 (not (str/blank? (:uk strs)))))
    translations))

(deftest test-menu-translations-complete
  (testing "Every Node-based menu selector has both languages"
    (is (all-translated? u/menu-translations)))
  (testing "Every prompt-text selector has both languages"
    (is (all-translated? u/menu-prompt-translations)))
  (testing "Every MenuItem id has both languages"
    (is (all-translated? u/menu-item-translations))))
