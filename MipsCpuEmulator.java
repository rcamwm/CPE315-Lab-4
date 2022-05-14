/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
*/

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;

public class MipsCpuEmulator {
    private Instruction[] instructions;
    private int[] registers;
    private int[] memory;
    private int pc;

    private InstructionPc[] pipeline;
    private ArrayDeque<InstructionPc> backlog;
    private int ifIdPos;
    private int instructionsInPipeline;
    private int instructionsExecuted;
    private int cycles;
    private int lastPc;

    private int notTakenBranchPc;
    private boolean branchFlag;
    private boolean loadFlag;
    private boolean jumpFlag;

    final static private int PIPE_SIZE = 4;

    MipsCpuEmulator(Instruction[] instructions)
    {
        this.instructions = instructions;
        this.registers = new int[32];
        this.memory = new int[8192];
        this.pipeline = new InstructionPc[PIPE_SIZE];
        this.backlog = new ArrayDeque<InstructionPc>();
        reset();
    }

    public void reset()
    {
        this.pc = 0;

        this.backlog.clear();
        this.ifIdPos = 0;
        this.instructionsInPipeline = 0;
        this.instructionsExecuted = 0;
        this.cycles = 0;
        this.lastPc = 0;

        this.notTakenBranchPc = 0;
        this.branchFlag = false;
        this.loadFlag = false;
        this.jumpFlag = false;

        Arrays.fill(this.registers, 0);
        Arrays.fill(this.memory, 0);
        Arrays.fill(this.pipeline, null);
    }

    public void runSingleCycle(int steps)
    {
        for (int s = 0; s < steps; s++)
        {
            if (this.pc < instructions.length)
            {
                pipelineInstruction(instructions[this.pc], this.pc);
                this.pc = instructions[this.pc].executeInstruction(this.pc, registers, memory);
            }
            else
                pipelineInstruction();
        }        
    }

    private void pipelineInstruction()
    {
        this.cycles++;
        checkForLoadFlag();
        updateInstructionExecuted();

        if (this.backlog.isEmpty())
        {
            this.pipeline[this.ifIdPos] = null;
            this.instructionsInPipeline--;
        }
        else
        {
            if (!addedStall())
                this.pipeline[this.ifIdPos] = this.backlog.poll();
            this.lastPc = this.pipeline[this.ifIdPos].nextPc;
        }
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);            
    }

    private void pipelineInstruction(Instruction instruction, int pc)
    {
        this.cycles++;
        applySquash(pc);
        checkForBranchJumpFlags(instruction, pc + 1);
        checkForLoadFlag();
        this.backlog.add(new InstructionPc(instruction, pc + 1));
        updateInstructionExecuted();

        if (this.pipeline[this.ifIdPos] == null)
            this.instructionsInPipeline++;
        if (!addedStall())
            this.pipeline[this.ifIdPos] = this.backlog.poll();
        this.lastPc = this.pipeline[this.ifIdPos].nextPc;
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);            
    }

    public void runAllCycles()
    {
        while (this.pc < instructions.length)
        {
            pipelineInstruction(instructions[this.pc], this.pc);
            this.pc = instructions[this.pc].executeInstruction(this.pc, registers, memory);
        }
        while (this.instructionsInPipeline > 0)
            pipelineInstruction();
    }

    private void updateInstructionExecuted()
    {
        if (this.pipeline[this.ifIdPos] != null &&
        !(this.pipeline[this.ifIdPos].instruction instanceof Instruction_Blank))
        {
            this.instructionsExecuted++;
        }
    }

    private void checkForBranchJumpFlags(Instruction instruction, int nextPc)
    {
        if (instruction instanceof Instruction_I)
        {
            Instruction_I instruction_i = (Instruction_I)instruction;
            if (instruction_i.getOpCode() == Operations.opTable.get("beq") ||
                instruction_i.getOpCode() == Operations.opTable.get("bne"))
            {
                this.branchFlag = true;
                this.notTakenBranchPc = nextPc;
            }
        }
        else if (instruction instanceof Instruction_J || 
            (instruction instanceof Instruction_R) && 
            ((Instruction_R)instruction).getFunctCode() == Operations.functTable.get("jr"))
        {
            this.jumpFlag = true;
        }
    }

    private void checkForLoadFlag()
    {
        int idExe = incrementPipelinePointer(this.ifIdPos, -2);
        int ifId = incrementPipelinePointer(this.ifIdPos, -1);
        if (this.pipeline[ifId] != null && this.pipeline[idExe] != null &&
            this.pipeline[idExe].instruction instanceof Instruction_I &&
            ((Instruction_I)this.pipeline[idExe].instruction).useAfterLoad(this.pipeline[ifId].instruction))
        {
            this.loadFlag = true;
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

    private boolean addedStall()
    {
        if (this.loadFlag)
        {
            int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
            this.pipeline[this.ifIdPos] = this.pipeline[idExePos];
            this.pipeline[idExePos] = new InstructionPc(
                new Instruction_Blank(false), this.pipeline[this.ifIdPos].nextPc);
            this.loadFlag = false;
            return true;
        }
        return false;
        
    }

    private int incrementPipelinePointer(int value, int amount)
    {
        return (value + amount + PIPE_SIZE) % PIPE_SIZE;
    }

    public void printRegisterState()
    {
        HashSet<Integer> skipReg = new HashSet<>(Arrays.asList(1, 26, 27, 28, 30));
        int newLine = 0;
        System.out.println("\npc = " + this.pc);
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

    public void displayMemory(int num1, int num2)
    {
        System.out.println();
        for (int i = num1; i <= num2; i++)
        {
            System.out.println("[" + i + "] = " + memory[i]);
        }
        System.out.println();
    }

    public void printPipeline()
    {
        System.out.println("\npc	if/id	id/exe	exe/mem	mem/wb");
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, -1);

        if (this.pipeline[this.ifIdPos] == null)
            System.out.print(this.lastPc + "\t");
        else
            System.out.print(this.pipeline[this.ifIdPos].nextPc + "\t");

        for (int i = 0; i < PIPE_SIZE; i++)
        {
            if (this.pipeline[this.ifIdPos] == null)
                System.out.print("empty ");
            else
                System.out.print(this.pipeline[this.ifIdPos].instruction.getMnemonic() + " ");
            this.ifIdPos = incrementPipelinePointer(this.ifIdPos, -1);
        }
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);
        System.out.println("\n");
        printCpi();
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
