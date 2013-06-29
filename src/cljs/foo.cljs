(ns foo
  (:require-macros
    [btest.macros :refer [doit]])
  (:require
    [tailrecursion.priority-map :as p]))

(defn ^:export bar [x y] (doit x y))
