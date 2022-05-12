/*
Cameron McGiffert 
CPE315 Section 1
Lab 3
 */

public class lab4 {
    public static void main(String[] args)
    {
        String fileName = args[0];
        Instruction[] instructions = MipsAssembler.getInstructions(fileName);
        MipsDebugger debugger = new MipsDebugger(instructions);
        if (args.length == 1)
            debugger.run();
        else
        {
            String script = args[1];
            debugger.run(script);
        }
    }
}
