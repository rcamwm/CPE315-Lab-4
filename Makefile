lab4:
	javac lab4.java

tests:
	make test1
	make test2
	make test3
	make test4

test1:
	java lab4 lab4_fib10.asm lab4_fib10.script > cfib10
	diff -w -B cfib10 lab4_fib10.output
	rm cfib10

test2:
	java lab4 lab4_fib20.asm lab4_fib20.script > cfib20
	diff -w -B cfib20 lab4_fib20.output
	rm cfib20

test3:
	java lab4 lab4_test1.asm lab4_test1.script > ctest1
	diff -w -B ctest1 lab4_test1.output
	rm ctest1

test4:
	java lab4 lab4_test2.asm lab4_test2.script > ctest2
	diff -w -B ctest2 lab4_test2.output
	rm ctest2

delC: 
	rm *.class

dlab4:
	rm *.class
	javac lab4.java

turnin:
	handin jseng 315_lab4_1 Makefile
	handin jseng 315_lab4_1 *.java

move:
	mv * //home/rmcgiffe/CPE315lab4