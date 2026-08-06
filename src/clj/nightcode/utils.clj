(ns nightcode.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [nightcode.spec :as spec]
            [clojure.spec.alpha :as s :refer [fdef]])
  (:import [java.io File]
           [java.nio.file Paths]
           [javafx.scene.control Alert Alert$AlertType ButtonType]
           [javafx.scene Scene]
           [javafx.stage Modality]))

(def ^:const clojure-exts #{"boot" "clj" "cljc" "cljs" "cljx" "edn" "pxi" "hl" "carp"})

(definterface Bridge
  (onload [])
  (onautosave [])
  (onchange [])
  (onenter [text])
  (oneval [code]))

(fdef get-relative-path
  :args (s/cat :project-path string? :selected-path string?)
  :ret string?)

(defn get-relative-path
  "Returns the selected path as a relative URI to the project path."
  [project-path selected-path]
  (-> (Paths/get (.toURI (io/file project-path)))
      (.relativize (Paths/get (.toURI (io/file selected-path))))
      (.toString)))

(fdef delete-parents-recursively!
  :args (s/cat :project-set set? :path string?))

(defn delete-parents-recursively!
  "Deletes the given file along with all empty parents unless they are in project-set."
  [project-set path]
  (let [f (io/file path)]
    (when (and (zero? (count (.listFiles f)))
               (not (contains? project-set path)))
      (io/delete-file f true)
      (->> f
           .getParentFile
           .getCanonicalPath
           (delete-parents-recursively! project-set))))
  nil)

(fdef delete-children-recursively!
  :args (s/cat :path spec/file?))

(defn delete-children-recursively!
  "Deletes the children of the given dir along with the dir itself."
  [f]
  (when (.isDirectory f)
    (doseq [f2 (.listFiles f)]
      (delete-children-recursively! f2)))
  (io/delete-file f)
  nil)

(fdef get-project-root-path
  :args (s/cat :pref-state map?)
  :ret (s/nilable string?))

(defn get-project-root-path
  "Returns the root path that the selected path is contained within."
  [pref-state]
  (when-let [^String selected-path (:selection pref-state)]
    (-> #(or (.startsWith selected-path (str % File/separator))
           (= selected-path %))
        (filter (:project-set pref-state))
        first)))

(fdef build-systems
  :args (s/cat :path string?)
  :ret (s/coll-of keyword?))

(defn build-systems
  "Returns a set containing :lein if the given path contains project.clj.
   Boot support has been removed (deprecated since 2018)."
  [^String path]
  (let [file (io/file path)
        dir? (.isDirectory file)
        types #{}
        types (if (and dir? (.exists (io/file file "project.clj")))
                (conj types :lein)
                types)]
    types))

(fdef get-project-path
  :args (s/alt
          :one-arg (s/cat :pref-state map?)
          :two-args (s/cat :path string? :pref-state map?))
  :ret (s/nilable string?))

(defn get-project-path
  "Returns the project path that the given path is contained within."
  ([pref-state]
   (when-let [^String selected-path (:selection pref-state)]
     (get-project-path selected-path pref-state)))
  ([path pref-state]
   (if-let [file (try (io/file path) (catch Exception _))]
     (if (or (-> path build-systems count pos?)
             (contains? (:project-set pref-state) path))
       path
       (when-let [parent-file (.getParentFile file)]
         (get-project-path (.getCanonicalPath parent-file) pref-state)))
     path)))

(fdef parent-path?
  :args (s/cat :parent-path string? :child-path (s/nilable string?))
  :ret boolean?)

(defn parent-path?
  "Determines if the given parent path is equal to or a parent of the child."
  [^String parent-path ^String child-path]
  (or (= parent-path child-path)
      (and parent-path
           child-path
           (.isDirectory (io/file parent-path))
           (.startsWith child-path (str parent-path File/separator)))
      false))

(fdef get-extension
  :args (s/cat :path string?)
  :ret string?)

(defn get-extension
  "Returns the extension in the given path name."
  [^String path]
  (->> (.lastIndexOf path ".")
       (+ 1)
       (subs path)
       str/lower-case))

(fdef uri->str
  :args (s/cat :uri #(instance? java.net.URI %))
  :ret string?)

(defn uri->str
  "Converts a java.net.URI to a String."
  [uri]
  (-> uri Paths/get .normalize .toString))

(fdef get-exec-uri
  :args (s/cat :class-name string?)
  :ret #(instance? java.net.URI %))

(defn get-exec-uri
  "Returns the executable as a java.net.URI."
  [class-name]
  (-> (Class/forName class-name)
      .getProtectionDomain
      .getCodeSource
      .getLocation
      .toURI))

(fdef remove-returns
  :args (s/cat :s string?)
  :ret string?)

(defn remove-returns [^String s]
  (-> s
      (str/escape {\return ""})
      (str/replace #"\u001b[^\n]*" "")))

(fdef get-boot-path!
  :args (s/cat)
  :ret string?)

(defn get-boot-path! []
  ;; DEPRECATED: Boot is no longer maintained. This function exists only for
  ;; backward compatibility and will be removed in a future version.
  (binding [*out* *err*]
    (println "WARNING: Boot build system is deprecated and no longer supported."))
  (let [file-name "boot-2.7.2.jar"
        file (io/file (System/getProperty "user.home") (str ".nightcode-" file-name))]
    (when-not (.exists file)
      (-> file-name io/resource io/input-stream (io/copy file)))
    (.getCanonicalPath file)))

(fdef get-boot-tasks
  :args (s/cat :project-path string?)
  :ret (s/coll-of string?))

(defn get-boot-tasks [project-path]
  (try
    (let [path (.getCanonicalPath (io/file project-path "build.boot"))
          rdr (java.io.PushbackReader. (io/reader path))]
      (loop [tasks []]
        (if-let [form (try (read rdr)
                        (catch Exception _))]
          (if (= 'deftask (first form))
            (recur (conj tasks (str (second form))))
            (recur tasks))
          tasks)))
    (catch Exception _ [])))

(fdef normalize-text-size
  :args (s/cat :num number?)
  :ret (s/and number? even?))

(defn normalize-text-size [n]
  (-> n
      (/ 2)
      (Math/ceil)
      (* 2)
      (max 12)
      (min 24)
      int))

(fdef filter-paths
  :args (s/cat :paths (s/coll-of string?))
  :ret (s/coll-of string?))

(defn filter-paths [paths]
  (set (filter (fn [path]
                 (try (-> path io/file .exists)
                   (catch Exception _ false)))
         paths)))

(fdef show-warning!
  :args (s/cat :scene spec/scene? :title string? :header-text string?)
  :ret boolean?)

(defn show-warning! [^Scene scene ^String title ^String header-text]
  (let [dialog (doto (Alert. Alert$AlertType/CONFIRMATION)
                 (.setTitle title)
                 (.setHeaderText header-text)
                 (.setGraphic nil)
                 (.initOwner (.getWindow scene))
                 (.initModality Modality/WINDOW_MODAL))]
    (-> dialog .showAndWait (.orElse nil) (= ButtonType/OK))))

(fdef update-webviews!
  :args (s/cat :pref-state map? :runtime-state map?))

(defn update-webviews! [pref-state {:keys [editor-panes projects]}]
  (doseq [pane (concat (vals editor-panes) (map :pane (vals projects)))
          :when pane]
    (doseq [webview (.lookupAll pane "WebView")]
      (try
        (doto (.getEngine webview)
          (.executeScript (case (:theme pref-state)
                            :dark "changeTheme(true)"
                            :light "changeTheme(false)"))
          (.executeScript (format "setTextSize(%s)" (:text-size pref-state))))
        (catch Exception _)))))

(fdef get-icon-path
  :args (s/cat :file spec/file?)
  :ret (s/nilable string?))

(defn get-icon-path
  [f]
  (when-not (.isDirectory f)
    (let [ext (get-extension (.getName f))]
      (case ext
        "clj" "images/file-clj.png"
        "cljc" "images/file-cljc.png"
        "cljs" "images/file-cljs.png"
        "java" "images/file-java.png"
        (if (clojure-exts ext)
          "images/file-clj.png"
          "images/file.png")))))

(fdef sanitize-name
  :args (s/cat :s string?)
  :ret string?)

(defn sanitize-name [s]
  (as-> s $
        (str/trim $)
        (str/lower-case $)
        (str/replace $ "'" "")
        (str/replace $ #"[^a-z0-9]" " ")
        (str/split $ #" ")
        (remove empty? $)
        (str/join "-" $)))

; ui translations

(def ^:const menu-translations
  "CSS selectors (id or class) for translatable Node-based controls, mapped to their text per language."
  {"#new_file" {:en "New File" :uk "Новий файл"}
   "#open_in_file_browser" {:en "Open in File Browser" :uk "Відкрити у провіднику"}
   "#save" {:en "Save" :uk "Зберегти"}
   "#undo" {:en "Undo" :uk "Скасувати"}
   "#redo" {:en "Redo" :uk "Повторити"}
   "#restart" {:en "Restart REPL" :uk "Перезапустити REPL"}
   "#start" {:en "Start" :uk "Старт"}
   "#theme_dark" {:en "Dark" :uk "Темна"}
   "#theme_light" {:en "Light" :uk "Світла"}
   "#font_dec" {:en "Font -" :uk "Шрифт -"}
   "#font_inc" {:en "Font +" :uk "Шрифт +"}
   "#import_project" {:en "Import" :uk "Імпорт"}
   "#rename" {:en "Rename" :uk "Перейменувати"}
   "#remove" {:en "Remove" :uk "Видалити"}
   ".run-with-repl" {:en "Run with REPL" :uk "Запустити з REPL"}
   ".reload-file" {:en "Reload File" :uk "Перезавантажити файл"}
   ".reload-selection" {:en "Reload Selection" :uk "Перезавантажити виділене"}
   ".stop" {:en "Stop" :uk "Зупинити"}
   ".run" {:en "Run" :uk "Запустити"}
   ".build" {:en "Build" :uk "Зібрати"}
   ".clean" {:en "Clean" :uk "Очистити"}
   ".test" {:en "Test" :uk "Тестувати"}})

(def ^:const menu-prompt-translations
  "CSS selectors for translatable prompt text, mapped to their text per language."
  {"#find" {:en "Find" :uk "Пошук"}})

(def ^:const menu-item-translations
  "fx:ids of MenuItem/CheckMenuItem controls, mapped to their text per language.
   MenuItems aren't part of the Node scene graph, so they can't be reached via
   lookupAll and need to be matched by id explicitly."
  {"new_console_project" {:en "Console Project" :uk "Консольний проєкт"}
   "new_graphics_project" {:en "Graphics Project" :uk "Графічний проєкт"}
   "new_web_project" {:en "Web Project" :uk "Веб проєкт"}
   "new_game_project" {:en "Game Project" :uk "Ігровий проєкт"}
   "new_music_project" {:en "Music Project" :uk "Музичний проєкт"}
   "clone_from_git" {:en "Clone from Git" :uk "Клонувати з Git"}
   "auto_save" {:en "Auto Save" :uk "Автозбереження"}})

(fdef apply-translations!
  :args (s/cat :node spec/node? :lang keyword?))

(defn apply-translations!
  "Sets the text of every translatable control found within the given node
   (and its descendants) to match the given language (:en or :uk)."
  [node lang]
  (doseq [[selector strs] menu-translations
          ctrl (.lookupAll node selector)]
    (.setText ctrl (get strs lang (:en strs))))
  (doseq [[selector strs] menu-prompt-translations
          ctrl (.lookupAll node selector)]
    (.setPromptText ctrl (get strs lang (:en strs))))
  ; MenuItems live in a MenuButton's popup, not the main scene graph, so walk
  ; them explicitly instead of relying on lookupAll.
  (doseq [menu-button (.lookupAll node "MenuButton")
          item (.getItems menu-button)]
    (if (instance? javafx.scene.control.CustomMenuItem item)
      (apply-translations! (.getContent item) lang)
      (when-let [strs (get menu-item-translations (.getId item))]
        (.setText item (get strs lang (:en strs)))))))

