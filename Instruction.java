/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public abstract class Instruction
{
    public abstract void executeInstruction(int pc, int[] registers, int[] memory);
    public abstract void printBinary();
    public abstract String getMnemonic();
    public abstract boolean isBranchInstruction();
    public abstract boolean isJumpInstruction();
    
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