#boot/configuration
{:boot {:coordinates #{[reply "0.2.0"]}
        :directories #{"src/clj"}}
 :pom {:project tailrecursion/btest
       :version "0.1.0-SNAPSHOT"
       :description "FIXME"}} 

(ns user
  (:require
    [tailrecursion.boot.middleware.cljsbuild  :as cljs]
    [tailrecursion.boot.middleware.sync       :as sync]
    [tailrecursion.boot.middleware.watch      :as watch]
    [tailrecursion.boot.middleware.time       :as time]
    [clojure.java.io                          :as io]
    [reply.main                               :as repl]))

(def stage (tmp/mkdir ::stage "stage"))

(def cfg {:cljsbuild {:source-paths #{"src/cljs"}
                      :output-to (io/file stage "main.js")
                      :output-dir (tmp/mkdir ::output-dir)
                      :optimizations :simple}})

(def once (-> identity
            (sync/sync-time "resources/public" stage) 
            cljs/cljsbuild
            (time/time "Compiling ClojureScript...")))

(def auto (-> once
            (watch/watch-time {"src/cljs" ["cljs"], "src/clj" ["clj"]})
            (watch/loop-msec 100)))

(repl/launch-nrepl {})
