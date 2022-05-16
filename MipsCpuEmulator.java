/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
*/

import java.util.Arrays;
import java.util.HashSet;

public class MipsCpuEmulator 
{
    private Instruction[] instructions;
    private int[] registers;
    private int[] memory;
    private int pc;

    private Pipeline pipeline;
    private int instructionsExecuted;
    private int cycles;
    private boolean pcOverwritten;

    MipsCpuEmulator(Instruction[] instructions)
    {
        this.instructions = instructions;
        this.registers = new int[32];
        this.memory = new int[8192];
        this.pipeline = new Pipeline();
        reset();
    }

    public void reset()
    {
        this.pc = 0;
        this.instructionsExecuted = 0;
        this.cycles = 0;
        this.pcOverwritten = false;
        this.pipeline.clear();
        Arrays.fill(this.registers, 0);
        Arrays.fill(this.memory, 0);
    }

    public void runCycle()
    {
        this.cycles++;
        updateInstructionExecuted();
        this.pcOverwritten = false;

        int currentPc = this.pc;
        instructionMemoryAccess(currentPc);
        instructionExecute(currentPc);
        instructionDecode(currentPc);
        if (!this.pcOverwritten)
            instructionFetch(currentPc);
        this.pipeline.cycleIncrement();
    }

    private void instructionFetch(int currentPc)
    {
        if (currentPc < this.instructions.length)
            this.pipeline.writeIfId(this.instructions[currentPc]);
        else
            this.pipeline.clearIfId();
        this.pc++;
    }

    private void instructionDecode(int currentPc)
    {
        if (this.pipeline.getIdExe() != null)
        {
            Instruction decoded = this.pipeline.getIdExe();
            if (decoded.isJumpInstruction())
            {
                this.pipeline.writeIfId(new Instruction("squash", 0));
                if (decoded instanceof Instruction_J)
                {
                    ((Instruction_J)decoded).setReturnPc(currentPc);
                    this.pc = ((Instruction_J)decoded).getAddress();
                }
                else if (decoded instanceof Instruction_R)
                    this.pc = this.registers[((Instruction_R)decoded).getRs()];
                this.pcOverwritten = true;
            }
        }
    }

    private void instructionExecute(int currentPc)
    {
        if (this.pipeline.getExeMem() != null)
        {
            Instruction executed = this.pipeline.getExeMem();
            executed.executeInstruction(currentPc, this.registers, this.memory);
            if (this.pipeline.getIdExe() != null &&
                executed.useAfterLoad(this.pipeline.getIdExe()))
            {
                this.pipeline.writeIfId(this.pipeline.getIdExe());
                this.pipeline.writeIdExe(new Instruction("stall", 0));    
                this.pcOverwritten = true;
            }
        }
    }

    private void instructionMemoryAccess(int currentPc) 
    {
        if (this.pipeline.getMemWb() != null)
        {
            Instruction memoryAccessed = this.pipeline.getMemWb();
            if (memoryAccessed.isBranchInstruction())
            {
                int newPc = ((Instruction_I)memoryAccessed).getBranchPc();
                if (newPc != 0)
                {
                    ((Instruction_I)memoryAccessed).resetBranchPc();
                    this.pipeline.writeIfId(new Instruction("squash", 0));
                    this.pipeline.writeIdExe(new Instruction("squash", 0));
                    this.pipeline.writeExeMem(new Instruction("squash", 0));
                    this.pc = newPc;
                    this.pcOverwritten = true;
                }
            }
        }
    }

    public void runNCycles(int N)
    {
        for (int n = 0; n < N; n++)
            runCycle();
    }

    public void runAllCycles()
    {
        while (this.pc < instructions.length || this.pipeline.getInstructionCount() > 0)
            runCycle();
    }

    private void updateInstructionExecuted()
    {
        if (this.pipeline.getIfId() != null &&
            !this.pipeline.getIfId().getMnemonic().equals("squash") &&
            !this.pipeline.getIfId().getMnemonic().equals("stall"))
        {
            this.instructionsExecuted++;
        }
    }

    public void printRegisterState()
    {
        HashSet<Integer> skipReg = new HashSet<>(Arrays.asList(1, 26, 27, 28, 30));
        int newLine = 0;
        int printPc = (this.pc < 2) ? 0 : this.pc - 2;
        System.out.println("\npc = " + (printPc));
        for (int r = 0; r < Registers.registerArray.length; r++)
        {
            if (!skipReg.contains(r))
            {
                if (r == 0)
                    System.out.print(Registers.registerArray[r] + " = " + this.registers[r] + "           ");
                else if ((newLine + 1) % 4 == 0)
                    System.out.println(Registers.registerArray[r] + " = " + this.registers[r]);
                else
                    System.out.print(Registers.registerArray[r] + " = " + this.registers[r] + "          ");
                newLine++;
            }
        }
        System.out.println("\n");
    }

    public void printMemoryState(int num1, int num2)
    {
        System.out.println();
        for (int i = num1; i <= num2; i++)
            System.out.println("[" + i + "] = " + memory[i]);
        System.out.println();
    }

    public void printPipeline()
    {
        this.pipeline.printPipeline(this.pc);
    }

    public void printCpi()
    {
        double cpi = (double)this.cycles / (double)this.instructionsExecuted;
        System.out.println("\nProgram complete");
        System.out.print("CPI = " + String.format("%.3f", cpi) + " ");
        System.out.print("Cycles = " + this.cycles + " ");
        System.out.println("Instructions = " + this.instructionsExecuted + "\n");
    }
}