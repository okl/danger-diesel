(ns diesel.core-test
  (:use clojure.test
        diesel.core))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))


(def special-ops #{'+ '- '/ '*})

(defn special-op? [expr]
  (special-ops (first expr)))

(definterp my-interp []
  ['add => :add]
  ['sub => :sub]
  ['div => :div]
  [special-op? => :special-op])

(defmethod my-interp :add [[_ & args]]
  (apply +  (map my-interp args)))

(defmethod my-interp :sub [[_ & args]]
  (if (= (count args) 1)
    (my-interp (first args))
    (apply - (map my-interp args))))

(defmethod my-interp :div [[_ dividend divisor]]
  (/ dividend divisor))

(defmethod my-interp :special-op [[op & args]]
  (if (= (count args) 1)
    (my-interp (first args))
    (apply @(resolve op) (map my-interp args))))


(def x 10)
(my-interp '(add 6 7))
(my-interp '(add 6 x))

(my-interp '(+ 25 (add 6 (sub 10 x))))

(my-interp '(add 26))

(my-interp '(div 10 20))
