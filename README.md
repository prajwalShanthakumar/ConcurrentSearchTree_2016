# ConcurrentSearchTree_2016
Java implementation of a non-blocking k-ary search tree 

The code contains the implementation of [1] but extended to work without an automatic garbage collector. 
i.e memory recycling is performed without relying on the automatic garbage collector.

- Test.java creates the threads and the search tree on which they will execute operations.
- SearchTree.java contains the implementation of the concurrent search tree 
- The other files contains classes/structs used in SearchTree.java 
 
The K-ary search tree can be tested by running the following command
from within the src folder:

java k_ary.K_ary p o r i d c

Eg: java k_ary.K_ary 16 100000 1000 9 1 90

where

	- p is the number of threads you want to create
	- o is the total number of operations to be performed by the threads
	- r (>0) is the upper limit of the key range that will be inserted into the search tree (contention parameter)
		(Keys in the range (0-r) will be randomly inserted, searched for and deleted from the tree)
	- i:d:c is the ratio of insert:delete:contains operations 
	(a typical application will involve lots of contains() operations, few inserts() and even fewer deletes())
	
The program will output the total run time (sum of the times taken by each thread) in milliseconds

The code will currently run for K = 4.
To test various values of K, simply change the variable K in the file Node.java, recompile and run

Reference:

[1] T. Brown and J. Helga, “Non-blocking k-ary Search Trees,” Lecture Notes in Computer Science Principles of Distributed Systems, pp. 207–221, 2011.
