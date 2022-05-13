/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class Instruction_I extends Instruction
{
    private String mneumonic;
    private int op;
    private int rs;
    private int rt;
    private int immediate;

    public Instruction_I(String mneumonic, int op, int rt, int rs, int immediate)
    {
        this.mneumonic = mneumonic;
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.immediate = immediate;
    }

    public String getMnemonic()
    {
        return this.mneumonic;
    }

    public int getOpCode()
    {
        return this.op;
    }

    public int executeInstruction(int pc, int[] registers, int[] memory)
    {
        if (this.op == 4)
            pc = beq(pc, registers);
        else if (this.op == 5)
            pc = bne(pc, registers);
        else if (this.op == 8)
            addi(registers);
        else if (this.op == 35)
            lw(registers, memory);
        else if (this.op == 43)
            sw(registers, memory);
        return pc + 1;       
    }

    private int beq(int pc, int[] registers)
    {
        if (registers[this.rs] == registers[this.rt])
            pc += immediate;
        return pc;
    }

    private int bne(int pc, int[] registers)
    {
        if (registers[this.rs] != registers[this.rt])
            pc += immediate;
        return pc;
    }

    private void addi(int[] registers)
    {
        registers[this.rt] = registers[this.rs] + this.immediate;
    }

    private void lw(int[] registers, int[] memory)
    {
        registers[this.rt] = memory[registers[this.rs] + immediate];
    }

    private void sw(int[] registers, int[] memory)
    {
        memory[registers[this.rs] + immediate] = registers[this.rt];
    }

    public void printBinary()
    {
        System.out.print(String.format("%6s", Integer.toBinaryString(this.op)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.rs)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.rt)).replace(" ", "0") + " ");
        if (immediate < 0)
            System.out.println(Integer.toBinaryString(this.immediate).substring(16));
        else
            System.out.println(String.format("%16s", Integer.toBinaryString(this.immediate)).replace(" ", "0"));
    }
}
