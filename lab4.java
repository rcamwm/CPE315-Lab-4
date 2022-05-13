/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

public class lab4 {
    public static void main(String[] args)
    {
        String fileName = "lab4_fib10.asm";
        //String fileName = args[0];
        Instruction[] instructions = MipsAssembler.getInstructions(fileName);
        MipsDebugger debugger = new MipsDebugger(instructions);
        debugger.run("lab4_fib10.script");

        // if (args.length == 1)
        //     debugger.run();
        // else
        // {
        //     String script = args[1];
        //     debugger.run(script);
        // }
    }
}
