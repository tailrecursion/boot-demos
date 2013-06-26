(boot/install '{:coordinates #{[reply "0.2.0"]}})

(ns user
  (:require
    [tailrecursion.boot.middleware.cljsbuild  :as cljs]
    [tailrecursion.boot.middleware.sync       :as sync]
    [tailrecursion.boot.middleware.watch      :as watch]
    [tailrecursion.boot.middleware.time       :as time]
    [clojure.java.io                          :as io]
    [reply.main                               :as repl]))

(boot/add ["src/clj"])

(def stage (tmp/mkdir ::stage "stage"))

(def build (-> identity
             (sync/sync-time "resources/public" stage) 
             cljs/cljsbuild
             (time/time "Compiling ClojureScript...")
             (watch/watch-time {"src/cljs" ["cljs"]})
             (watch/loop-msec 100)))

(def cfg {:cljsbuild {:source-paths #{"src/cljs"}
                      :output-to (io/file stage "main.js")
                      :output-dir (tmp/mkdir ::output-dir)
                      :optimizations :simple}})

(repl/launch-nrepl {})
