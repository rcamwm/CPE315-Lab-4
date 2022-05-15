/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class lab4 {
    public static void main(String[] args)
    {
        final boolean DEBUG = false;
        if (DEBUG)
        {
            String[] filenames = {"lab4_fib10", "lab4_fib20", "lab4_test1", "lab4_test2"};
            String fileName = filenames[1];

            Instruction[] instructions = MipsAssembler.getInstructions(fileName + ".asm");
            MipsDebugger debugger = new MipsDebugger(instructions);
            debugger.run(fileName + ".script");
        }
        else 
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
}
