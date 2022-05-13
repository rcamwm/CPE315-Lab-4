public class Instruction_Blank extends Instruction
{   
    int type; // 1: Squash, 0: Stall
    Instruction_Blank(boolean trueForSquash_falseForStall) 
    {
        if (trueForSquash_falseForStall)
            this.type = 1;
        else
            this.type = 0;
    }

    public int executeInstruction(int pc, int[] registers, int[] memory)
    {
        return -1;
    }

    public void printBinary()
    {
        System.out.println();
    }

    public String getMnemonic()
    {
        if (type == 0)
            return "stall";
        return "squash";
    }
}
