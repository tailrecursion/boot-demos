;; configure project (merged into #'boot/env atom)
#boot/configuration
{:boot  {:coordinates  #{[reply "0.2.0"]
                         [tailrecursion/hoplon "1.1.0-SNAPSHOT"]}
         :directories  #{"src/clj"}}
 :pom   {:project      tailrecursion/btest
         :version      "0.1.0-SNAPSHOT"
         :description  "FIXME"}}

(ns user
  (:require
    [tailrecursion.boot.middleware.jar        :as jar]
    [tailrecursion.boot.middleware.util       :refer [after return]]
    [tailrecursion.boot.middleware.pom        :refer [wrap-pom]]
    [tailrecursion.boot.middleware.hoplon     :refer [hoplon]]
    [tailrecursion.boot.middleware.cljsbuild  :refer [cljsbuild]]
    [tailrecursion.boot.middleware.sync       :refer [sync-time]]
    [tailrecursion.boot.middleware.watch      :refer [watch-time]]
    [tailrecursion.boot.middleware.time       :refer [time]]
    [clojure.java.io                          :refer [file make-parents]]
    [reply.main                               :refer [launch-nrepl]])
  (:refer-clojure :exclude [time]))

(let [with-parents #(doto (apply file %&) make-parents)
      ;; output directories
      odir    (with-parents "resources/public")
      tdir    (with-parents "target")
      ;; source directories
      html    (file "src/html")
      static  (file "src/static")
      ;; temporary directories
      jsdir   (tmp/mkdir ::jsdir "jsdir")
      cljsdir (tmp/mkdir ::cljsdir "cljsdir")
      stage   (tmp/mkdir ::stage "stage")
      build   (tmp/mkdir ::build "build")
      target  (tmp/mkdir ::target "target")
      js-out  (file jsdir "main.js")
      ;; build message
      msg     "Compiling Hoplon..."
      ;; directories and file extensions to watch for changes
      wdirs   {"src/html"   #{"html" "cljs"}
               "src/clj"    #{"clj"}
               "src/cljs"   #{"cljs"}
               "src/static" nil}]

  ;; configure project (merged into #'boot/env atom)
  (boot/configure
    {:hoplon    {:source-dir    html
                 :html-out      stage
                 :cljs-out      cljsdir
                 :compiled-js   js-out}
     :cljsbuild {:source-paths  #{cljsdir "src/cljs"}
                 :output-to     js-out
                 :output-dir    build
                 :optimizations :whitespace}
     :jar       {:resources     #{"resources"}
                 :manifest      {"Micha-Says" "hello dood"}
                 :main          'foo.bar-baz
                 :output-dir    target}}) 

  ;; define build tasks by composing middleware
  (def once (-> identity cljsbuild hoplon (after sync-time odir stage static jsdir) (time msg)))
  (def auto (-> once (watch-time wdirs)))
  (def jar  (-> once wrap-pom (after jar/jar) (after sync-time tdir target)))

  (defn file-segments [f]
    (loop [out (list (.getName f)) f (.getParentFile f)]
      (if f (recur (cons (.getName f) out) (.getParentFile f)) out)))

  ;; convenience function to run tasks
  (def build #(do (% @boot/env) nil))

  ;; make-go:
  ;;
  ;; user=> (build once)
  ;; user=> (build auto)
  ;; user=> (build jar)

  ;; boot into a repl in the user ns
  (launch-nrepl {}))
