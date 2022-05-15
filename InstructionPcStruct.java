class InstructionPcStruct
{
    public Instruction instruction;
    public int nextPc;
    InstructionPcStruct(Instruction instruction, int nextPc)
    {
        this.instruction = instruction;
        this.nextPc = nextPc;
    }
}