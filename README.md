# Diesel

Building a DSL in Clojure _seems_ easy enough with multimethods, but
there's still some room for improvement for common enough patterns.

Diesel aims to be a light weight DSL engine, so you can quickly
describe a DSL with a nifty table of contents, but still take
advantage of the multimethod infrastructure set forth by clojure.

For a nice introduction to building DSLs with Multimethods in Clojure,
take a look at
[this blog post](http://pragprog.com/magazines/2011-07/growing-a-dsl-with-clojure)
that inspired me to create Diesel on top of multimethods.

Currently Diesel provides small, but useful improvement over above:

1. Centralized DSL operator definition
2. The ability to support custom functions to dispatch on
3. Default constant handling
4. Default unknown operator handling


## Usage
### Get

In your project.clj add

```
[com.onekingslane.danger/diesel "1.0.0"]
```

### Use
Currently there is an example usage [here](test/diesel/core_test.clj)
and the example illustrated by the afore mentioned blog post is
implemented [here](test/diesel/growing/a_dsl_with_clojure.clj):

### Caveats
#### Developing with nrepl

One of the poor side effects of using multimethods is that their
redefinition is quite tricky for Clojure to deal with.  This means
that if we use `definterpter` to define a small DSL as follows:

```
(definterpreter simple-interp []
  ['add => :add]
  ['sub => :sub]
	...)
```

... then we decide we want to add some additional functions like so:

```
(definterpreter simple-interp []
  ['add => :add]
  ['sub => :sub]
  ['div => :div]
  ['mult => :mult]
	...)
```

... we can't simply re-evaluate the buffer or expression. we have to, in
fact, kill the nrepl-server and restart the process.

You can, however, redefine the `defmethod` portions at will and
re-evaluate.

At some point, I will try to address this, but for now, I think it's
important to just be aware you cannot expand your language (or make
alterations) without bouncing your clojure process.






## License

Copyright © 2013 One Kings Lane

Distributed under the Eclipse Public License, the same as Clojure.
