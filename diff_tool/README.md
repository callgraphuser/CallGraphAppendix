# diff_for_CG

Loads the methods, transformes these methods to a unified format, and stores for every loader the original format.
After method transformation it transforms connections. If the method is transformed and the original format is stored it is just a simple search.

The program outputs:
- txt files: every loader is paired to every other. These files contain information about node, edge and common node and common edge num.
- tsv files: tab separated values. These files contain which method loaded which methods and connections.
- list files: it just a list of tsv files, for further usage. every file is prefixed in this list:L<name> means it is a loader; U<name> means it is the UnionGraph's tsv file. Others might be added.
- files that contains information about filtering.
- diff files: every call that could be (the loader has both of the methods) in an intersection but is not (connection were not created by the tool).
- csv files: for the Diagram maker
- non_fileter_methods: it prints every method before filtering.
  
  
all the files created: *.csv *.tsv *.list *.diff non* *.txt *.debug
