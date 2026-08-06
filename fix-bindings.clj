(ns fix-bindings
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn fix-file! [f]
  (let [content (slurp f)
        new-content (-> content
                        (str/replace #"\[this\]" "[_]")
                        (str/replace #"\[this event\]" "[_ _]")
                        (str/replace #"\[this observable old-value new-value\]" "[_ _ _ _]")
                        (str/replace #"\[this observable old-value\]" "[_ _ _]")
                        (str/replace #"\[this observable\]" "[_ _]")
                        (str/replace #"\[this code\]" "[_ code]")
                        (str/replace #"\[this dir\]" "[_ dir]")
                        (str/replace #"\[this text\]" "[_ _]")
                        (str/replace #"\[this unit\]" "[_ unit]")
                        (str/replace #"\[this total-tasks\]" "[_ total-tasks]")
                        (str/replace #"\[this total-work\]" "[_ total-work]")
                        (str/replace #"\[this scene\]" "[_ _]"))]
    (when (not= content new-content)
      (spit f new-content)
      (println "Fixed bindings in" (.getName f)))))

(doseq [f (file-seq (io/file "src/clj/nightcode"))
        :when (and (.isFile f) (str/ends-with? (.getName f) ".clj"))]
  (fix-file! f))
