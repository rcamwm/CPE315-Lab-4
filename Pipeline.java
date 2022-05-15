/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

import java.util.Arrays;

public class Pipeline {
    final private int PIPESIZE = 4;
    private Instruction[] pipeline;
    private int ifIdPos;
    private int instructionCount;

    Pipeline()
    {
        this.pipeline = new Instruction[PIPESIZE];
        Arrays.fill(this.pipeline, null);
        this.ifIdPos = 0;
        this.instructionCount = 0;
    }

    public void cycleIncrement()
    {
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);
    }

    public void clear()
    {
        Arrays.fill(this.pipeline, null);
        this.ifIdPos = 0;
        this.instructionCount = 0;
    }

    public void writeIfId(Instruction instruction)
    {
        if (this.pipeline[this.ifIdPos] == null)
            this.instructionCount++;
        this.pipeline[this.ifIdPos] = instruction;
    }

    public void writeIdExe(Instruction instruction)
    {
        int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
        if (this.pipeline[idExePos] == null)
            this.instructionCount++;
        this.pipeline[idExePos] = instruction;
    }

    public void writeExeMem(Instruction instruction)
    {
        int exeMemPos = incrementPipelinePointer(this.ifIdPos, -2);
        if (this.pipeline[exeMemPos] == null)
            this.instructionCount++;
        this.pipeline[exeMemPos] = instruction;
    }

    public void clearIfId()
    {
        this.pipeline[this.ifIdPos] = null;
        this.instructionCount--;
    }

    public int getInstructionCount()
    {
        return this.instructionCount;
    }

    public Instruction getIfId()
    {
        return this.pipeline[this.ifIdPos];
    }

    public Instruction getIdExe()
    {
        int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
        return this.pipeline[idExePos];
    }

    public Instruction getExeMem()
    {
        int exeMemPos = incrementPipelinePointer(this.ifIdPos, -2);
        return this.pipeline[exeMemPos];
    }

    public Instruction getMemWb()
    {
        int memWbPos = incrementPipelinePointer(this.ifIdPos, -3);
        return this.pipeline[memWbPos];
    }

    private int incrementPipelinePointer(int value, int amount)
    {
        return (value + amount + PIPESIZE) % PIPESIZE;
    }

    public void printPipeline(int pc)
    {
        System.out.println("\npc	if/id	id/exe	exe/mem	mem/wb");
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, -1);

        System.out.print(pc + "\t");

        for (int i = 0; i < PIPESIZE; i++)
        {
            if (this.pipeline[this.ifIdPos] == null)
                System.out.print("empty ");
            else
                System.out.print(this.pipeline[this.ifIdPos].getMnemonic() + " ");
            this.ifIdPos = incrementPipelinePointer(this.ifIdPos, -1);
        }
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);
        System.out.println("\n");
    }

}
