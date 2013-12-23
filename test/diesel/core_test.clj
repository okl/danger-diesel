(ns diesel.core-test
  (:use clojure.test
        diesel.core))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Testing the simpliest, basic intepreter!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def special-ops #{'+ '- '/ '*})

(defn special-op? [expr]
  (special-ops (first expr)))

(definterp simple-interp []
  ['add => :add]
  ['sub => :sub]
  ['div => :div]
  ['mult => :mult]
  [special-op? => :special-op])

;; Look, you can destructure the args!
(defmethod simple-interp :add [[_ & args]]
  (apply +  (map simple-interp args)))

(defmethod simple-interp :sub [[_ & args]]
  (if (= (count args) 1)
    (simple-interp (first args))
    (apply - (map simple-interp args))))

;; Fixed argument destructuring!
(defmethod simple-interp :div [[_ dividend divisor]]
  (/ dividend divisor))

;; This function demonstrates no destructuring
(defmethod simple-interp :mult [expr]
  (let [op (first expr)
        args (rest expr)]
    (apply * args)))

;; ah, look! We can refer to the op, even when destructuring
(defmethod simple-interp :special-op [[op & args]]
  (if (= (count args) 1)
    (simple-interp (first args))
    (apply @(resolve op) (map simple-interp args))))

;;(def external-defed-var 10)

(deftest simple-interp-test
  (testing "A small interpreter..."
    (testing "symbol look up"
      (is (= 13 (simple-interp '(add 6 7)))))
    (testing "inner recursive interpretation"
      (is (= 11 (simple-interp '(add 6 (sub 10 5))))))
    ;; (testing "the use of externally defined variables"
    ;;   (= 16 (simple-interp '(add 6 external-defed-var))))
    (testing "using a predicate to look up a function"
      (is (= 31
             (simple-interp
              '(+ 25 (add 6 (sub 10 10)))))))
    (testing "using a predicate to look up a function"
      (is (= 125 (simple-interp '(mult 5 5 5)))))
    (testing "a constant"
      (is (= 5 (simple-interp 5))))
    (testing "an undefined operator"
      (is (thrown? RuntimeException (simple-interp '(+ 5 (yourmom 10))))))))
