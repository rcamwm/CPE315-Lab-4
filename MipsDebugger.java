/*
Cameron McGiffert 
CPE315 Section 1
Lab 4
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MipsDebugger {
    private MipsCpuEmulator cpu;

    MipsDebugger(Instruction[] instructions)
    {
        this.cpu = new MipsCpuEmulator(instructions);
    }

    public void run()
    {
        Scanner scan = new Scanner(System.in);
        char command = ' ';
        while (command != 'q')
        {
            command = runCommand(scan, false);
        }
        scan.close();
    }

    public void run(String scriptFileName)
    {
        try
        {
            File source = new File(scriptFileName);
            Scanner scan = new Scanner(source);
            while (scan.hasNextLine())
            {
                runCommand(scan, true);
            }
            scan.close();      
        }
        catch (FileNotFoundException exception)
        {
            exception.printStackTrace();
        }
    }

    private char runCommand(Scanner scan, boolean isScriptMode)
    {
        if (!isScriptMode)
            System.out.print("mips> ");

        String input = scan.nextLine();
        String[] arguments = input.split(" ");
        char command = input.charAt(0);

        if (isScriptMode)
        {
            System.out.print("mips>");
            for (int i = 0; i < arguments.length; i++)
                System.out.print(" " + arguments[i]);
            System.out.println();
        }

        switch (command)
        {
            case 'h':
                help();
                break;
            case 'd':
                cpu.printRegisterState();
                break;
            case 'p':
                this.cpu.printPipeline();
                break;
            case 's':
                if (arguments.length > 1)
                    this.cpu.runNCycles(Integer.parseInt(arguments[1]));
                else
                    this.cpu.runCycle();
                this.cpu.printPipeline();
                break;
            case 'r':
                cpu.runAllCycles();
                cpu.printCpi();
                break;
            case 'm':
                cpu.printMemoryState(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]));
                break;
            case 'c':
                System.out.println("        Simulator reset\n");
                cpu.reset();
                break;
            case 'q':
                break;
            default:
                System.out.println("Please provide valid input");
        }
        return command;
    }

    private void help()
    {
        System.out.println();
        System.out.println("h = show help");
        System.out.println("d = dump register state");
        System.out.println("p = show pipeline registers");
        System.out.println("s = step through a single clock cycle step (i.e. simulate 1 cycle and stop)");
        System.out.println("s num = step through num clock cycles");
        System.out.println("r = run until the program ends and display timing summary");
        System.out.println("m num1 num2 = display data memory from location num1 to num2");
        System.out.println("c = clear all registers, memory, and the program counter to 0");
        System.out.println("q = exit the program");
        System.out.println();
    }
}
