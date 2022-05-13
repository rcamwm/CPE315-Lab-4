/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

import java.util.HashMap;
public class Operations {
    static final public HashMap<String, Integer> opTable = new HashMap<String, Integer>();
    static 
    {
        opTable.put("j", 2);
        opTable.put("jal", 3);
        opTable.put("beq", 4);
        opTable.put("bne", 5);
        opTable.put("addi", 8);
        opTable.put("lw", 35);
        opTable.put("sw", 43);
    }

    static final public HashMap<String, Integer> functTable = new HashMap<String, Integer>();
    static {
        functTable.put("sll", 0);
        functTable.put("srl", 2);
        functTable.put("jr", 8);
        functTable.put("add", 32);
        functTable.put("sub", 34);
        functTable.put("and", 36);
        functTable.put("or", 37);
        functTable.put("slt", 42);
    }
}
