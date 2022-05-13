/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
*/

import java.util.ArrayDeque;

public class MipsCpuEmulator {
    private InstructionPc[] pipeline;
    private ArrayDeque<InstructionPc> backlog;
    private int ifPos;
    private int instructionsInPipeline;
    private int instructionsExecuted;
    private int cycles;
    private int lastPc;

    private boolean jumpFlag;
    private boolean branchFlag;
    private int notTakenBranchPc;

    final static private int PIPE_SIZE = 5;

    MipsCpuEmulator()
    {
        this.pipeline = new InstructionPc[PIPE_SIZE];
        this.backlog = new ArrayDeque<InstructionPc>();
        reset();
    }

    public void reset()
    {
        this.backlog.clear();
        this.ifPos = 0;
        this.instructionsInPipeline = 0;
        this.instructionsExecuted = 0;
        this.cycles = 0;
        this.lastPc = 0;

        this.jumpFlag = false;
        this.branchFlag = false;
        this.notTakenBranchPc = 0;

        for (int i = 0; i < PIPE_SIZE; i++)
            this.pipeline[i] = null;
    }

    public void runSingleCycle()
    {
        this.cycles++;
        updateInstructionExecuted();
        if (this.backlog.isEmpty())
        {
            this.pipeline[this.ifPos] = null;
            this.instructionsInPipeline--;
        }
        else
        {
            this.pipeline[this.ifPos] = this.backlog.poll();
            this.lastPc = this.pipeline[this.ifPos].nextPc;
        }
        this.ifPos = (this.ifPos + 1) % PIPE_SIZE;            
    }

    public void runSingleCycle(Instruction instruction, int pc)
    {
        this.cycles++;
        applySquash(pc);
        checkForFlags(instruction, pc + 1);
        this.backlog.add(new InstructionPc(instruction, pc + 1));
        updateInstructionExecuted();
        if (this.pipeline[this.ifPos] == null)
            this.instructionsInPipeline++;
        this.pipeline[this.ifPos] = this.backlog.poll();
        this.lastPc = this.pipeline[this.ifPos].nextPc;
        this.ifPos = (this.ifPos + 1) % PIPE_SIZE;            
    }

    public void runAllCycles()
    {
        while (this.instructionsInPipeline > 0)
        {
            runSingleCycle();
        }
    }

    private void updateInstructionExecuted()
    {
        if (this.pipeline[this.ifPos] != null &&
        !(this.pipeline[this.ifPos].instruction instanceof Instruction_Blank))
        {
            this.instructionsExecuted++;
        }
    }

    private void checkForFlags(Instruction instruction, int nextPc)
    {
        if (instruction instanceof Instruction_J || 
            (instruction instanceof Instruction_R) && 
            ((Instruction_R)instruction).getFunctCode() == Operations.functTable.get("jr"))
        {
            this.jumpFlag = true;
        }
        else if (instruction instanceof Instruction_I && 
            (((Instruction_I)instruction).getOpCode() == Operations.opTable.get("beq") ||
            ((Instruction_I)instruction).getOpCode() == Operations.opTable.get("bne")))
        {
            this.branchFlag = true;
            this.notTakenBranchPc = nextPc;
        }
    }

    private void applySquash(int nextPc)
    {
        if (this.jumpFlag)
        {
            this.jumpFlag = false;
            this.backlog.add(new InstructionPc(new Instruction_Blank(true), nextPc));
        }
        else if (this.branchFlag)
        {
            this.branchFlag = false;
            if (this.notTakenBranchPc != nextPc)
            {
                for (int i = 0; i < 3; i++)
                    this.backlog.add(new InstructionPc(
                        new Instruction_Blank(true), this.notTakenBranchPc + i + 1));
            }
        }

    }

    public void printPipeline()
    {
        System.out.println("\npc	if/id	id/exe	exe/mem	mem/wb");
        this.ifPos = (this.ifPos - 1 + PIPE_SIZE) % PIPE_SIZE;

        if (this.pipeline[this.ifPos] == null)
            System.out.print(this.lastPc + "\t");
        else
            System.out.print(this.pipeline[this.ifPos].nextPc + "\t");

        for (int i = 0; i < 4; i++, this.ifPos = (this.ifPos - 1 + PIPE_SIZE) % PIPE_SIZE)
        {
            if (this.pipeline[this.ifPos] == null)
                System.out.print("empty ");
            else
                System.out.print(this.pipeline[this.ifPos].instruction.getMnemonic() + " ");
        }
        System.out.println("\n");
    }

    public void printCpi()
    {
        double cpi = (double)this.cycles / (double)this.instructionsExecuted;
        System.out.println("\nProgram complete");
        System.out.print("CPI = " + String.format("%.2f", cpi) + " ");
        System.out.print("Cycles = " + this.cycles + " ");
        System.out.println("Instructions = " + this.instructionsExecuted + "\n");
    }
}

class InstructionPc
{
    public Instruction instruction;
    public int nextPc;
    InstructionPc(Instruction instruction, int nextPc)
    {
        this.instruction = instruction;
        this.nextPc = nextPc;
    }
}
