# 1.0.3

Forms may now be conses or lists BUT NOT VECTORS.

Allowing forms to be vectors was interfering with vector literals.

# 1.0.2

Forms may now be conses or vectors, not just lists.

Previously, forms would only be interpreted if they were lists; if
they were conses or vectors, they would be dispatched to the
:const-value mulitimethod.

Now, forms will be interpreted if they are seqs (which includes both
lists and conses) or vectors.
