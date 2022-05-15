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

    private Pipeline pipeline;
    private ArrayDeque<InstructionPcStruct> backlog;
    private int instructionsExecuted;
    private int cycles;
    private int lastPc;

    private int notTakenBranchPc;
    private boolean branchFlag;
    private boolean loadFlag;
    private boolean jumpFlag;
    private boolean pcOverwritten;

    MipsCpuEmulator(Instruction[] instructions)
    {
        this.instructions = instructions;
        this.registers = new int[32];
        this.memory = new int[8192];
        this.pipeline = new Pipeline();
        this.backlog = new ArrayDeque<InstructionPcStruct>();
        reset();
    }

    public void reset()
    {
        this.pc = 0;

        this.backlog.clear();
        this.instructionsExecuted = 0;
        this.cycles = 0;
        this.lastPc = 0;

        this.notTakenBranchPc = 0;
        this.branchFlag = false;
        this.loadFlag = false;
        this.jumpFlag = false;
        this.pcOverwritten = false;

        Arrays.fill(this.registers, 0);
        Arrays.fill(this.memory, 0);
        this.pipeline.clear();
    }

    public void cycle()
    {
        this.cycles++;
        updateInstructionExecuted();
        this.pcOverwritten = false;

        int currentPc = this.pc;
        instructionFetch(currentPc);
        instructionDecode(currentPc);
        instructionExecute(currentPc);
        instructionMemoryAccess(currentPc);

        if (!pcOverwritten)
            this.pc++;
        this.pipeline.cycleIncrement();
    }

    private void instructionFetch(int currentPc)
    {
        if (currentPc < this.instructions.length)
            this.pipeline.writeIfId(this.instructions[currentPc], currentPc + 1);
        else
            this.pipeline.clearIfId();
    }

    private void instructionDecode(int currentPc)
    {
        if (this.pipeline.getIdExe() != null)
        {
            Instruction decoded = this.pipeline.getIdExe().instruction;
            if (decoded.isJumpInstruction())
            {
                this.pipeline.writeIfId(new Instruction_Blank(true), currentPc + 1);
                if (decoded instanceof Instruction_J)
                {
                    ((Instruction_J)decoded).setReturnPc(this.pc);
                    this.pc = ((Instruction_J)decoded).getAddress();
                }
                else if (decoded instanceof Instruction_R)
                {
                    this.pc = this.registers[((Instruction_R)decoded).getRs()];
                    if (this.pc == 0) 
                        System.out.println("what");
                }
                    
                this.pcOverwritten = true;
            }
        }
    }

    private void instructionExecute(int currentPc)
    {
        if (this.pipeline.getExeMem() != null)
        {
            Instruction executed = this.pipeline.getExeMem().instruction;
            executed.executeInstruction(currentPc, this.registers, this.memory);
            if (this.pipeline.getIdExe() != null &&
                executed instanceof Instruction_I &&
                ((Instruction_I)executed).useAfterLoad(this.pipeline.getIdExe().instruction))
            {
                this.pipeline.writeIfId(this.pipeline.getIdExe().instruction, this.pipeline.getIdExe().nextPc);
                this.pipeline.writeIdExe(new Instruction_Blank(false), this.pipeline.getIfId().nextPc);    
                this.pcOverwritten = true;
            }
        }
    }

    private void instructionMemoryAccess(int currentPc) 
    {
        if (this.pipeline.getMemWb() != null)
        {
            Instruction memoryAccessed = this.pipeline.getMemWb().instruction;
            if (memoryAccessed.isBranchInstruction())
            {
                int newPc = ((Instruction_I)memoryAccessed).getBranchPc();
                if (newPc != 0)
                {
                    this.pipeline.writeIfId(new Instruction_Blank(true), currentPc + 1);
                    this.pipeline.writeIdExe(new Instruction_Blank(true), currentPc + 1);
                    this.pipeline.writeExeMem(new Instruction_Blank(true), currentPc + 1);
                    this.pc = newPc;
                    this.pcOverwritten = true;
                }
            }
        }
    }

    public void runSingleCycle(int steps)
    {
        for (int s = 0; s < steps; s++)
        {
            cycle();
            // if (this.pc < instructions.length)
            // {
            //     pipelineInstruction(instructions[this.pc], this.pc);
            //     this.pc = instructions[this.pc].executeInstruction(this.pc, registers, memory);
            // }
            // else
            //     pipelineInstruction();
        }        
    }

    private void pipelineInstruction()
    {
        this.cycles++;
        checkForLoadFlag();
        updateInstructionExecuted();

        if (this.backlog.isEmpty())
        {
            this.pipeline.clearIfId();
        }
        else
        {
            if (!addedStall())
            {
                InstructionPcStruct i = this.backlog.poll();
                this.pipeline.writeIfId(i.instruction, i.nextPc);
            }
            InstructionPcStruct j = this.pipeline.getIfId();
            this.lastPc = j.nextPc;
        }
        this.pipeline.cycleIncrement();
    }

    private void pipelineInstruction(Instruction instruction, int pc)
    {
        this.cycles++;
        applySquash(pc);
        checkForBranchJumpFlags(instruction, pc + 1);
        checkForLoadFlag();
        this.backlog.add(new InstructionPcStruct(instruction, pc + 1));
        updateInstructionExecuted();

        if (!addedStall())
        {
            InstructionPcStruct i = this.backlog.poll();
            this.pipeline.writeIfId(i.instruction, i.nextPc);
        }
        InstructionPcStruct j = this.pipeline.getIfId();
        this.lastPc = j.nextPc;
        this.pipeline.cycleIncrement();     
    }

    public void runAllCycles()
    {
        while (this.pc < instructions.length || this.pipeline.getInstructionCount() > 0)
        {
            cycle();
            printPipeline();
        }
    }

    private void updateInstructionExecuted()
    {
        if (this.pipeline.getIfId() != null &&
            !(this.pipeline.getIfId().instruction instanceof Instruction_Blank))
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
        if (this.pipeline.getIdExe() != null && this.pipeline.getExeMem() != null &&
            this.pipeline.getExeMem().instruction instanceof Instruction_I &&
            ((Instruction_I)this.pipeline.getExeMem().instruction).useAfterLoad(this.pipeline.getIdExe().instruction))
        {
            this.loadFlag = true;
        }
        // int idExe = incrementPipelinePointer(this.ifIdPos, -2);
        // int ifId = incrementPipelinePointer(this.ifIdPos, -1);
        // if (this.pipeline[ifId] != null && this.pipeline[idExe] != null &&
        //     this.pipeline[idExe].instruction instanceof Instruction_I &&
        //     ((Instruction_I)this.pipeline[idExe].instruction).useAfterLoad(this.pipeline[ifId].instruction))
        // {
        //     this.loadFlag = true;
        // }
    }

    private void applySquash(int nextPc)
    {
        if (this.jumpFlag)
        {
            this.jumpFlag = false;
            this.backlog.add(new InstructionPcStruct(new Instruction_Blank(true), nextPc));
        }
        else if (this.branchFlag)
        {
            this.branchFlag = false;
            if (this.notTakenBranchPc != nextPc)
            {
                for (int i = 0; i < 3; i++)
                    this.backlog.add(new InstructionPcStruct(
                        new Instruction_Blank(true), this.notTakenBranchPc + i + 1));
            }
        }
    }

    private boolean addedStall()
    {
        if (this.loadFlag)
        {
            this.pipeline.writeIfId(this.pipeline.getIdExe().instruction, this.pipeline.getIdExe().nextPc);
            this.pipeline.writeIdExe(new Instruction_Blank(false), this.pipeline.getIfId().nextPc);
            this.loadFlag = false;
            return true;
        }
        return false;
        
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
        this.pipeline.printPipeline(this.pc);
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
