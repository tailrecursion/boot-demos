(boot/install '{:coordinates #{[reply "0.2.0"]}})

(require '[tailrecursion.boot.middleware.cljsbuild :refer [cljsbuild]])
(require '[tailrecursion.boot.middleware.cljsbuild :refer [cljsbuild]])
(require '[tailrecursion.boot.middleware.watch :refer [watch-time loop-msec]])
(require '[clojure.pprint :refer [pprint]])
(require '[clojure.java.io :refer [make-parents file copy]])
(require '[reply.main :refer [launch-nrepl]])

(boot/add ["src/clj"])

(defn wrap-build [handler]
  (fn [spec]
    (println "Compiling ClojureScript...") 
    (let [retspec (handler spec)
          js-tmp  (get-in retspec [:cljsbuild :output])
          js-out  (file (:js-out spec))]
      (make-parents js-out)
      (copy js-tmp js-out)
      retspec)))

(defn wrap-done [handler] (fn [spec] (handler spec) (prn :ok)))

(def build (-> identity
             cljsbuild
             wrap-build
             wrap-done
             (watch-time {"src/cljs" ["cljs"]})
             (loop-msec 100)))

(def cfg {:js-out "resources/public/main.js"
          :cljsbuild {:source-paths #{"src/cljs"}
                      :output-dir (tmp/mkdir ::output-dir)
                      :optimizations :simple}})

(launch-nrepl {})
