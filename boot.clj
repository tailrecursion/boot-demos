#boot/configuration
{:boot {:coordinates #{[reply "0.2.0"]}
        :directories #{"src/clj"}}
 :pom {:project tailrecursion/btest
       :version "0.1.0-SNAPSHOT"
       :description "FIXME"}}

(ns user
  (:require
    [tailrecursion.boot.middleware.cljsbuild  :refer [cljsbuild]]
    [tailrecursion.boot.middleware.sync       :refer [sync-time]]
    [tailrecursion.boot.middleware.watch      :refer [watch-time]]
    [tailrecursion.boot.middleware.time       :refer [time]]
    [clojure.java.io                          :refer [file]]
    [reply.main                               :refer [launch-nrepl]])
  (:refer-clojure :exclude [time]))

(let [odir  (file "resources/public")
      stage (tmp/mkdir ::stage "stage")
      msg   "Compiling ClojureScript..."]

  (boot/configure
    {:cljsbuild {:source-paths #{"src/cljs"}
                 :output-to (file stage "main.js")
                 :output-dir (tmp/mkdir ::output-dir)
                 :optimizations :whitespace}}) 

  (def once (-> identity (sync-time odir stage) cljsbuild (time msg)))
  (def auto (-> once (watch-time {"src/cljs" ["cljs"] "src/clj" ["clj"]})))

  (launch-nrepl {})) 
