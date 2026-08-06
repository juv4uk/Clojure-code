(ns nightcode.builders-test
  (:require [clojure.test :refer [deftest is testing]]
            [nightcode.builders :as b]))

;; --- create-pipes ---

(deftest test-create-pipes
  (testing "create-pipes returns correct structure"
    (let [pipes (b/create-pipes)]
      (is (map? pipes))
      (is (contains? pipes :in))
      (is (contains? pipes :out))
      (is (contains? pipes :in-pipe))
      (is (contains? pipes :out-pipe))))
  (testing "Pipes are correct types"
    (let [{:keys [in out in-pipe out-pipe]} (b/create-pipes)]
      (is (instance? clojure.lang.LineNumberingPushbackReader in))
      (is (instance? java.io.PrintWriter out))
      (is (instance? java.io.PipedReader in-pipe))
      (is (instance? java.io.PipedWriter out-pipe)))))
