public class Instruction_Blank extends Instruction
{   
    String mneumonic;
    Instruction_Blank(boolean trueForSquash_falseForStall) 
    {
        if (trueForSquash_falseForStall)
            this.mneumonic = "squash";
        else
            this.mneumonic = "stall";
    }

    public String getMnemonic()
    {
        return this.mneumonic;
    }

    public void executeInstruction(int pc, int[] registers, int[] memory) {}

    public boolean isBranchInstruction() { return false; }

    public boolean isJumpInstruction() { return false; }

    public void printBinary() {}

    @Override
    public String toString()
    {
        return this.mneumonic;
    }
}
