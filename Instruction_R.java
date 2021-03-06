/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class Instruction_R extends Instruction
{
    private int rs;
    private int rt;
    private int rd;
    private int shamt;
    private int funct;

    public Instruction_R(String mneumonic, int funct, int rd, int rs, int rt)
    {
        super(mneumonic, 0);
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.shamt = 0;
        this.funct = funct;
    }

    public Instruction_R(String mneumonic, char shiftDirection, int rd, int rt, int shamt)
    {
        super(mneumonic, 0);
        this.rs = 0;
        this.rt = rt;
        this.rd = rd;
        this.shamt = shamt;
        if (shiftDirection == 'l')
            this.funct = 0;
        else // 'r'
            this.funct = 2;
    }

    public Instruction_R(String mneumonic, int funct, int rs)
    {
        super(mneumonic, 0);
        this.rs = rs;
        this.rt = 0;
        this.rd = 0;
        this.shamt = 0;
        this.funct = funct;
    }

    public int getRs()
    {
        return this.rs;
    }

    public int getRt()
    {
        return this.rt;
    }

    public int getFunctCode()
    {
        return this.funct;
    }

    @Override
    public void executeInstruction(int pc, int[] registers, int[] memory)
    {
        if (this.funct == 0)
            sll(registers);
        else if (this.funct == 2)
            srl(registers);
        else if (this.funct == 8)
            jr(registers);
        else if (this.funct == 32)
            add(registers);
        else if (this.funct == 34)
            sub(registers);
        else if (this.funct == 36)
            and(registers);
        else if (this.funct == 37)
            or(registers);
        else if (this.funct == 42)
            slt(registers);
    }

    private void sll(int[] registers)
    {
        registers[this.rd] = registers[this.rt] << this.shamt;
    }

    private void srl(int[] registers)
    {
        registers[this.rd] = registers[this.rt] >>> this.shamt;
    }

    private int jr(int[] registers)
    {
        return registers[this.rs];
    }

    private void add(int[] registers)
    {
        registers[this.rd] = registers[this.rs] + registers[this.rt];
    }

    private void sub(int[] registers)
    {
        registers[this.rd] = registers[this.rs] - registers[this.rt];
    }

    private void and(int[] registers)
    {
        registers[this.rd] = registers[this.rs] & registers[this.rt];
    }

    private void or(int[] registers)
    {
        registers[this.rd] = registers[this.rs] | registers[this.rt];
    }

    private void slt(int[] registers)
    {
        if (registers[this.rs] < registers[this.rt])
            registers[this.rd] = 1;
        else
            registers[this.rd] = 0;
    }

    @Override
    public boolean isJumpInstruction()
    {
        if (this.funct == 8)
            return true;
        return false;
    }

    @Override
    public void printBinary()
    {
        System.out.print(String.format("%6s", Integer.toBinaryString(this.op)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.rs)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.rt)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.rd)).replace(" ", "0") + " ");
        System.out.print(String.format("%5s", Integer.toBinaryString(this.shamt)).replace(" ", "0") + " ");
        System.out.println(String.format("%6s", Integer.toBinaryString(this.funct)).replace(" ", "0"));
    }

    @Override
    public String toString()
    {
        if (this.funct == 0 || this.funct == 2)
        {
            return String.format("%s %3s", this.mneumonic, " ") +
                Registers.registerArray[this.rd] + ", " +
                Registers.registerArray[this.rt] + ", " +
                Registers.registerArray[this.shamt];
        }
        else if (this.funct == 8)
        {
            return String.format("%s %3s", this.mneumonic, " ") +
                Registers.registerArray[this.rs];
        }
        return String.format("%s %3s", this.mneumonic, " ") +
            Registers.registerArray[this.rd] + ", " +
            Registers.registerArray[this.rs] + ", " +
            Registers.registerArray[this.rt];
    }
}

