/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

import java.util.HashMap;
public class Registers
{
    static final public HashMap<String, Integer> registerTable = new HashMap<String, Integer>();
    static
    {
        registerTable.put("$zero", 0);
        registerTable.put("$0", 0);
        registerTable.put("$at", 1);
        registerTable.put("$v0", 2);
        registerTable.put("$v1", 3);
        registerTable.put("$a0", 4);
        registerTable.put("$a1", 5);
        registerTable.put("$a2", 6);
        registerTable.put("$a3", 7);
        registerTable.put("$t0", 8);
        registerTable.put("$t1", 9);
        registerTable.put("$t2", 10);
        registerTable.put("$t3", 11);
        registerTable.put("$t4", 12);
        registerTable.put("$t5", 13);
        registerTable.put("$t6", 14);
        registerTable.put("$t7", 15);
        registerTable.put("$s0", 16);
        registerTable.put("$s1", 17);
        registerTable.put("$s2", 18);
        registerTable.put("$s3", 19);
        registerTable.put("$s4", 20);
        registerTable.put("$s5", 21);
        registerTable.put("$s6", 22);
        registerTable.put("$s7", 23);
        registerTable.put("$t8", 24);
        registerTable.put("$t9", 25);
        registerTable.put("$k0", 26);
        registerTable.put("$k1", 27);
        registerTable.put("$gp", 28);
        registerTable.put("$sp", 29);
        registerTable.put("$fp", 30);
        registerTable.put("$ra", 31);
    }
    static final public String[] registerArray = {
        "$0", "$$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
        "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
        "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
        "$t8", "$t9", "$k0", "$k1", "$gp", "$sp", "$fp", "$ra",
    };
}