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
[com.onekingslane.danger/diesel "1.1.0"]
```

### Use
Currently there is an example usage [here](test/diesel/core_test.clj)
and the example illustrated by the afore mentioned blog post is
implemented [here](test/diesel/growing/a_dsl_with_clojure.clj):



## License

Copyright © 2013 One Kings Lane

Distributed under the Eclipse Public License, the same as Clojure.
