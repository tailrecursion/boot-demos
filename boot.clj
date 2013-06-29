#boot/configuration
{:boot {:coordinates #{[reply "0.2.0"]
                       [tailrecursion/hoplon "1.1.0-SNAPSHOT"]}
        :directories #{"src/clj"}}
 :pom {:project tailrecursion/btest
       :version "0.1.0-SNAPSHOT"
       :description "FIXME"}}

(ns user
  (:require
    [tailrecursion.boot.middleware.jar        :as jar]
    [tailrecursion.boot.middleware.util       :refer [after return]]
    [tailrecursion.boot.middleware.hoplon     :refer [hoplon]]
    [tailrecursion.boot.middleware.cljsbuild  :refer [cljsbuild]]
    [tailrecursion.boot.middleware.sync       :refer [sync-time]]
    [tailrecursion.boot.middleware.watch      :refer [watch-time]]
    [tailrecursion.boot.middleware.time       :refer [time]]
    [clojure.java.io                          :refer [file]]
    [reply.main                               :refer [launch-nrepl]])
  (:refer-clojure :exclude [time]))

(let [odir    (file "resources/public")
      tdir    (file "target")
      html    (file "src/html")
      static  (file "src/static")
      stage   (tmp/mkdir ::stage "stage")
      build   (tmp/mkdir ::build "build")
      target  (tmp/mkdir ::target "target")
      msg     "Compiling Hoplon..."
      wdirs   {"src/html"   ["html" "cljs"]
               "src/clj"    ["clj"]
               "src/cljs"   ["cljs"]
               "src/static" nil}]

  (boot/configure
    {:hoplon    {:source-dir    html
                 :html-out      stage}
     :cljsbuild {:source-paths  #{"src/cljs"}
                 :output-dir    build
                 :optimizations :whitespace}
     :jar       {:directories   ["resources" "src/clj" "src/cljs"]
                 :output-dir    target}}) 

  (def once (-> identity cljsbuild hoplon (after sync-time odir stage static) (time msg)))
  (def auto (-> once (watch-time wdirs)))
  (def jar  (-> once (after jar/jar) (after sync-time tdir target)))
  (def run  #(do (% @boot/env) nil))

  (launch-nrepl {})) 
