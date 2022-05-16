/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class Instruction
{
    String mneumonic;
    int op;

    Instruction(String mneumonic, int op)
    {
        this.mneumonic = mneumonic;
        this.op = op;
    }

    public void executeInstruction(int pc, int[] registers, int[] memory) {}

    public String getMnemonic() { return this.mneumonic; }
    public int getOpCode() { return this.op; }

    public boolean isBranchInstruction() { return false; }
    public boolean useAfterLoad(Instruction instruction) { return false; }
    public boolean isJumpInstruction() { return false; }
    public void printBinary() {}
    
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