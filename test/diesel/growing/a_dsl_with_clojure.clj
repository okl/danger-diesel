(ns diesel.growing.a-dsl-with-clojure
  "Using Diesel to implement
http://pragprog.com/magazines/2011-07/growing-a-dsl-with-clojure

You should read this blog post before attempting to understand
what this file is doing."
  {:author "Alex Bahouth"
   :date "12/23/2013"}
  (:use clojure.test
        diesel.core))

;; Heh, since we handle constants
;; to be defined as (not lists)
;; This is pretty small!

(def ^{:dynamic true} *current-impl*)
(derive ::bash ::common)
(derive ::batch ::common)

(definterpreter shell-interp []
  ['println => [*current-impl* :println]]
  ['statements => [*current-impl* :statements]])

(defmethod shell-interp [::bash :println] [[_ val]]
  (str "echo " val))

(defmethod shell-interp [::batch :println] [[_ val]]
  (str "ECHO " val))

(defmethod shell-interp [::common :statements] [[_ & statements]]
  (str (clojure.string/join "; " (map shell-interp statements) )))

(deftest multi-shell-interp-test
  (testing "Testing to make sure we can use dynamic
bindings to switch between our implementation routes to
 share common implementation"
    (is (= "echo \"hello\"; echo \"world\""
           (binding [*current-impl* ::bash]
             (shell-interp
              '(statements (println "\"hello\"")
                           (println "\"world\""))))))
    (is (= "ECHO \"hello\"; ECHO \"world\""
           (binding [*current-impl* ::batch]
             (shell-interp
              '(statements (println "\"hello\"")
                           (println "\"world\""))))))))
