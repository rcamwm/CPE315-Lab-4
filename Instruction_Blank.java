public class Instruction_Blank extends Instruction
{   
    String mnemonic;
    Instruction_Blank(boolean trueForSquash_falseForStall) 
    {
        if (trueForSquash_falseForStall)
            this.mnemonic = "squash";
        else
            this.mnemonic = "stall";
    }

    public String getMnemonic()
    {
        return this.mnemonic;
    }

    public void executeInstruction(int pc, int[] registers, int[] memory) {}

    public boolean isBranchInstruction() { return false; }

    public boolean isJumpInstruction() { return false; }

    public void printBinary() {}
}
