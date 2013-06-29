#boot/configuration
{:boot {:coordinates #{[reply "0.2.0"]
                       [tailrecursion/hoplon "1.1.0-SNAPSHOT"]}
        :directories #{"src/clj"}}
 :pom {:project tailrecursion/btest
       :version "0.1.0-SNAPSHOT"
       :description "FIXME"}}

(ns user
  (:require
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
      html    (file "src/html")
      static  (file "src/static")
      stage   (tmp/mkdir ::stage "stage")
      build   (tmp/mkdir ::build "build")
      msg     "Compiling Hoplon..."
      wdirs   {"src/html"   ["html" "cljs"]
               "src/clj"    ["clj"]
               "src/cljs"   ["cljs"]
               "src/static" nil}]

  (boot/configure
    {:hoplon    {:source-dir    "src/html"
                 :html-out      stage}
     :cljsbuild {:source-paths  #{"src/cljs"}
                 :output-dir    build
                 :optimizations :whitespace}}) 

  (def once (-> identity cljsbuild hoplon (after sync-time odir stage static) (time msg) return))
  (def auto (-> once (watch-time wdirs)))

  (launch-nrepl {})) 
