(ns foo
  (:require-macros
    [btest.macros :refer [doit]]
    )
  )

(defn ^:export bar [x y] (doit x y))
