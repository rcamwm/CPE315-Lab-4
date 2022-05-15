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
    private int branchPc; // Only for branch instructions

    public Instruction_I(String mneumonic, int op, int rt, int rs, int immediate)
    {
        this.mneumonic = mneumonic;
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.immediate = immediate;
        this.branchPc = 0;
    }

    public String getMnemonic()
    {
        return this.mneumonic;
    }

    public int getOpCode()
    {
        return this.op;
    }

    public int getRs()
    {
        return this.rs;
    }

    public int getRt()
    {
        return this.rt;
    }

    public int getImmediate()
    {
        return this.immediate;
    }

    public int getBranchPc()
    {
        return this.branchPc;
    }

    public void resetBranchPc()
    {
        this.branchPc = 0;
    }

    public boolean useAfterLoad(Instruction instruction)
    {
        if (this.op == 35 && this.rt != 0)
        {
            if (instruction instanceof Instruction_R)
            {
                Instruction_R r = (Instruction_R)instruction;
                if (this.rt == r.getRs() || this.rt == r.getRt())
                    return true;
            }
            else if (instruction instanceof Instruction_I)
            {
                Instruction_I i = (Instruction_I)instruction;
                if (this.rt == i.getRs())
                    return true;
            }
        }
        return false;
        
    }

    public boolean usesRegister(int reg)
    {
        if (reg == this.rs || reg == this.rt)
            return true;
        return false;
    }

    public void executeInstruction(int pc, int[] registers, int[] memory)
    {
        if (this.op == 4)
            beq(pc, registers);
        else if (this.op == 5)
            bne(pc, registers);
        else if (this.op == 8)
            addi(registers);
        else if (this.op == 35)
            lw(registers, memory);
        else if (this.op == 43)
            sw(registers, memory);   
    }

    private void beq(int pc, int[] registers)
    {
        if (registers[this.rs] == registers[this.rt])
        {
            pc = pc - 1 + this.immediate; // -1 because beq is executed 2 cycles after pipeline insertion
            this.branchPc = pc;
        }
    }

    private void bne(int pc, int[] registers)
    {
        if (registers[this.rs] != registers[this.rt])
        {
            pc = pc - 1 + this.immediate; // -1 because bne is executed 2 cycles after pipeline insertion
            this.branchPc = pc;
        }
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

    public boolean isBranchInstruction()
    {
        if (this.op == 4 || this.op == 5)
            return true;
        return false;
    }

    public boolean isJumpInstruction()
    {
        return false;
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

    @Override
    public String toString()
    {
        if (this.op == 4 || this.op == 5)
        {
            return String.format("%s %3s", this.mneumonic, " ") +
                Registers.registerArray[this.rt] + ", " +
                Registers.registerArray[this.rs] + ", " +
                this.immediate + 
                " # -> " + this.branchPc;
        }
        return String.format("%s %3s", this.mneumonic, " ") +
            Registers.registerArray[this.rt] + ", " +
            Registers.registerArray[this.rs] + ", " +
            this.immediate;
    }
}
