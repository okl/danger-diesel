(ns diesel.core
  "A small _diesel_ engine to build your own DSLs!"
  {:author "Alex Bahouth"
   :date "12/22/2013"}
  (:use [roxxi.utils.print]))

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
