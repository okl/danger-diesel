(ns diesel.core
  "A small _diesel_ engine to build your own DSLs!"
  {:author "Alex Bahouth"
   :date "12/22/2013"}
  (:use [roxxi.utils.print]))

(defn- of-form? [fn-or-op expr]
  (if (fn? fn-or-op)
    (fn-or-op expr)
    (and (list? expr)
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

(defmacro definterp [name formals & dispatch-mappings]
  (let [dispatch-mappings (vec (map remove-dispatch-form-cruft dispatch-mappings))
        expr+formals (vec (cons 'expr formals))]
    `(do
       (defmulti ~name
         (fn ~'intepreter-fn ~expr+formals
           (if (list? ~'expr)
             (or (ordered-expr-interp ~'expr ~dispatch-mappings)
                 :unknown-operator)
             :const-value)))
       (defmethod ~name :const-value ~expr+formals
         (if (symbol? ~'expr)
           @(resolve ~'expr)
           ~'expr))
       (defmethod ~name :unknown-operator ~expr+formals
         (str "Unknown handler for `" ~'expr
              "` when handling `" ~'expr "`")))))
