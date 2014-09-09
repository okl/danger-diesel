(ns diesel.core
  "A small _diesel_ engine to build your own DSLs!"
  {:author "Alex Bahouth"
   :date "12/22/2013"}
  (:require [roxxi.utils.print :refer [print-expr]])
  (:require [roxxi.utils.collections :refer [walk-apply]]))

(defmacro seqish? [thing]
  `(seq? ~thing))

(defn- of-form? [fn-or-op expr]
  (if (fn? fn-or-op)
    (fn-or-op expr)
    (and (seqish? expr)
         (not (empty? expr))
         (= (first expr) fn-or-op))))

(defn- remove-dispatch-form-cruft
  "Our dispatch forms have `=>` to make them pretty.
Since that isn't valid syntax, this removes that value
and effectively turns `[a => b]` into `[a b]`."
  [dispatch-expr]
  [(first dispatch-expr) (last dispatch-expr)])

(defn- ordered-expr-interp [expr dispatch-mappings]
   (loop [op-fns=>ds dispatch-mappings]
    (when op-fns=>ds
      (let [op-fn=>d (first op-fns=>ds)
            op-fn (first op-fn=>d)
            d-val (last op-fn=>d)]
        (if (of-form? op-fn expr)
          d-val
          (recur (next op-fns=>ds)))))))

;; # The money

(defmacro definterpreter [name formals & dispatch-mappings]
  (let [dispatch-mappings (vec (map remove-dispatch-form-cruft dispatch-mappings))
        expr+formals (vec (cons 'expr formals))
        ;; this rebinding prevents needing to export ordered-expr-interp
        ;; and issues, and lets us change our dispatching algorithm.
        expr-interp ordered-expr-interp]
    `(do
       (def ~name nil) ;; resets the binding should we be re-evaluating
       (defmulti ~name
         (fn ~'intepreter-fn ~expr+formals
           (if (seqish? ~'expr)
             (or (~expr-interp ~'expr ~dispatch-mappings)
                 :unknown-operator)
             :const-value)))
       (defmethod ~name :const-value ~expr+formals
         (if (symbol? ~'expr)
           (name ~'expr)
           ~'expr))
       (defmethod ~name :unknown-operator ~expr+formals
         (throw
          (RuntimeException.
           (str "Unknown handler for `" ~'expr
                "` when handling `" ~'expr "`")))))))

;; # Convenience functions

(defn- denamespace-symbol
  "For symbols that are fully namespace-qualified, removes the namespace
  qualification. For all other objects, returns the object unmodified."
  [thing]
  (if (symbol? thing)
    (symbol (name thing))
    thing))

(defn denamespace-form
  "Walks an entire form, denamespacing any symbols it finds.

  You would use this when you've used syntax-quote (`) to build up a form
  for the interpreter. While syntax-quote is very convenient (since it allows
  unquoting), it has the side-effect of fully namespace-resolving all symbols.

  Thus, `(my-interp (language-elem ...)) becomes
  '(my-ns/my-interp (my-ns/language-elem ...)), which is not parsable by your
  interpreter.

  denamespace-form would remove all namespace-qualifications on all the symbols
  in the syntax-quoted form, allowing you to use syntax-quoting when working with
  diesel interpreters.
    (denamespace-form '(my-ns/my-interp (my-ns/language-elem ...)))
     => '(my-interp (language-elem ...))

  That's what this function is for."
  [form]
  (walk-apply form denamespace-symbol))
