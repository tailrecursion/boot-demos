(boot/install '{:coordinates #{[reply "0.2.0"]}})

(require '[tailrecursion.boot.middleware.cljsbuild :refer [cljsbuild]])
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

(defn wrap-done [handler] (fn [spec] (handler spec) :ok))

(def build (-> identity cljsbuild wrap-build wrap-done))

(def cfg {:js-out "resources/public/main.js"
          :cljsbuild {:source-paths   #{"src/cljs"}
                      :output-dir     ".out"
                      :optimizations  :simple}})

(launch-nrepl {})
