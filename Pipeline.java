import java.util.Arrays;

public class Pipeline {
    final private int PIPESIZE = 4;
    private InstructionPcStruct[] pipeline;
    private int ifIdPos;
    private int instructionCount;

    Pipeline()
    {
        this.pipeline = new InstructionPcStruct[PIPESIZE];
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

    public void writeIfId(Instruction instruction, int nextPc)
    {
        if (this.pipeline[this.ifIdPos] == null)
            this.instructionCount++;
        this.pipeline[this.ifIdPos] = new InstructionPcStruct(instruction, nextPc);
    }

    public void writeIdExe(Instruction instruction, int nextPc)
    {
        int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
        if (this.pipeline[idExePos] == null)
            this.instructionCount++;
        this.pipeline[idExePos] = new InstructionPcStruct(instruction, nextPc);
    }

    public void writeExeMem(Instruction instruction, int nextPc)
    {
        int exeMemPos = incrementPipelinePointer(this.ifIdPos, -2);
        if (this.pipeline[exeMemPos] == null)
            this.instructionCount++;
        this.pipeline[exeMemPos] = new InstructionPcStruct(instruction, nextPc);
    }

    public void clearIfId()
    {
        this.pipeline[this.ifIdPos] = null;
        this.instructionCount--;
    }

    public void stallIfId(Instruction newIdExeInstruction, int nextPc)
    {
        int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
        this.pipeline[this.ifIdPos] = this.pipeline[idExePos];
        this.pipeline[idExePos] = new InstructionPcStruct(newIdExeInstruction, nextPc); 
    }

    public int getInstructionCount()
    {
        return this.instructionCount;
    }

    public InstructionPcStruct getIfId()
    {
        return this.pipeline[this.ifIdPos];
    }

    public InstructionPcStruct getIdExe()
    {
        int idExePos = incrementPipelinePointer(this.ifIdPos, -1);
        return this.pipeline[idExePos];
    }

    public InstructionPcStruct getExeMem()
    {
        int exeMemPos = incrementPipelinePointer(this.ifIdPos, -2);
        return this.pipeline[exeMemPos];
    }

    public InstructionPcStruct getMemWb()
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
                System.out.print(this.pipeline[this.ifIdPos].instruction.getMnemonic() + " ");
            this.ifIdPos = incrementPipelinePointer(this.ifIdPos, -1);
        }
        this.ifIdPos = incrementPipelinePointer(this.ifIdPos, 1);
        System.out.println("\n");
    }

}
