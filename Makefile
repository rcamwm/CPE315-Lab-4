lab3:
	javac lab3.java

test1:
	java lab3 lab3_fib.asm lab3_fib.script > cfib
	diff -w -B cfib lab3_fib.output

test2:
	java lab3 lab3_sum10.asm lab3_sum10.script > csum10
	diff -w -B csum10 lab3_sum10.output

test3:
	java lab3 lab3_test3.asm lab3_test3.script > ctest3
	diff -w -B ctest3 lab3_test3.output

delC: # delete .class files and my comparison files
	rm cfib
	rm csum10
	rm ctest3
	rm Instruction_I.class
	rm Instruction_J.class
	rm Instruction_R.class
	rm Instruction.class
	rm lab3.class
	rm MipsAssembler.class
	rm MipsDebugger.class
	rm Operations.class
	rm Registers.class

turnin:
	handin jseng 315_lab3_1 Makefile
	handin jseng 315_lab3_1 Instruction.java
	handin jseng 315_lab3_1 Instruction_I.java
	handin jseng 315_lab3_1 Instruction_J.java
	handin jseng 315_lab3_1 Instruction_R.java
	handin jseng 315_lab3_1 lab3.java
	handin jseng 315_lab3_1 MipsAssembler.java
	handin jseng 315_lab3_1 MipsDebugger.java
	handin jseng 315_lab3_1 Operations.java
	handin jseng 315_lab3_1 Registers.java

move:
	mv Makefile //home/rmcgiffe/CPE315Lab3
	mv Instruction_I.java //home/rmcgiffe/CPE315Lab3
	mv Instruction_J.java //home/rmcgiffe/CPE315Lab3
	mv Instruction_R.java //home/rmcgiffe/CPE315Lab3
	mv Instruction.java //home/rmcgiffe/CPE315Lab3
	mv lab3.java //home/rmcgiffe/CPE315Lab3
	mv MipsAssembler.java //home/rmcgiffe/CPE315Lab3
	mv MipsDebugger.java //home/rmcgiffe/CPE315Lab3
	mv Operations.java //home/rmcgiffe/CPE315Lab3
	mv Registers.java //home/rmcgiffe/CPE315Lab3
