/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public abstract class Instruction
{
    public abstract int executeInstruction(int pc, int[] registers, int[] memory); // Returns address of next instruction
    public abstract void printBinary();
    public abstract String getMnemonic();
    
    static public void invalidInstructionError(String op)
    {
        System.out.println("invalid instruction: " + op);
        System.exit(0);
    }

    static public char getInstructionType(String op)
    {
        if (Operations.opTable.containsKey(op))
        {
            if (Operations.opTable.get(op) == 2 || Operations.opTable.get(op) == 3)
                return 'J';
            else
                return 'I';
        }
        else if (Operations.functTable.containsKey(op))
            return 'R';
            
        return 0;
    }
}