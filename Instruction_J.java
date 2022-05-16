/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class Instruction_J extends Instruction
{
    private int address;
    private int returnPc; // For jal instructions

    public Instruction_J(String mneumonic, int op, int address)
    {
        super(mneumonic, op);
        this.address = address;
        this.returnPc = 0;
    }

    public int getAddress()
    {
        return this.address;
    }

    public void setReturnPc(int currentPc)
    {
        this.returnPc = currentPc;
    }

    public int getReturnPc()
    {
        return this.returnPc;
    }

    @Override
    public void executeInstruction(int pc, int[] registers, int[] memory)
    {
        if (this.op == 3)
            jal(registers);
    }

    private void jal(int[] registers)
    {
        registers[Registers.registerTable.get("$ra")] = this.returnPc;
    } // +0 because returnPc is set 1 cycle after insertion, and so as to not rerun jal

    @Override
    public boolean isJumpInstruction()
    {
        return true;
    }

    @Override
    public void printBinary()
    {
        System.out.print(String.format("%6s", Integer.toBinaryString(this.op)).replace(" ", "0") + " ");
        System.out.println(String.format("%26s", Integer.toBinaryString(this.address)).replace(" ", "0"));
    }

    @Override
    public String toString()
    {
        return String.format("%s %3s", this.mneumonic, " ") + "    " + this.address;
    }
}