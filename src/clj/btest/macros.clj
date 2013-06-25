(ns btest.macros)

(defmacro doit [a b]
  `(+ ~a ~b ~a ~b))
