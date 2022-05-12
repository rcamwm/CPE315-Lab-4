/*
Cameron McGiffert 
CPE315 Section 1
Lab 3
 */

import java.util.Scanner;
import java.util.HashSet;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;

public class MipsDebugger {
    private int pc;
    private int[] registers;
    private int[] memory;
    private Instruction[] instructions;

    MipsDebugger(Instruction[] instructions)
    {
        this.pc = 0;
        this.registers = new int[32];
        this.memory = new int[8192];
        this.instructions = instructions;
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
        String input = scan.nextLine().toLowerCase();
        String[] arguments = input.split(" ");
        char command = input.charAt(0);

        if (isScriptMode)
        {
            System.out.print("mips>");
            for (int i = 0; i < arguments.length; i++)
                System.out.print(" " + arguments[i]);
            System.out.println();
        }
        else
            System.out.print("mips> ");

        switch (command)
        {
            case 'h':
                help();
                break;
            case 'd':
                dumpRegisterState();
                break;
            case 's':
                if (arguments.length > 1)
                    singleStep(Integer.parseInt(arguments[1]));
                else
                    singleStep(1);
                break;
            case 'r':
                runProgram();
                break;
            case 'm':
                displayMemory(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]));
                break;
            case 'c':
                clear();
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
        System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
        System.out.println("s num = step through num instructions of the program");
        System.out.println("r = run until the program ends");
        System.out.println("m num1 num2 = display data memory from location num1 to num2");
        System.out.println("c = clear all registers, memory, and the program counter to 0");
        System.out.println("q = exit the program");
        System.out.println();
    }

    private void dumpRegisterState()
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

    private void singleStep(int steps)
    {
        int s = 0;
        for (; s < steps && this.pc < instructions.length; s++)
            this.pc = instructions[this.pc].executeInstruction(this.pc, registers, memory);
        System.out.println("        " + s + " instruction(s) executed");
    }

    private void runProgram()
    {
        while (this.pc < instructions.length)
            this.pc = instructions[this.pc].executeInstruction(this.pc, registers, memory);      
    }

    private void displayMemory(int num1, int num2)
    {
        System.out.println();
        for (int i = num1; i <= num2; i++)
        {
            System.out.println("[" + i + "] = " + memory[i]);
        }
        System.out.println();
    }

    private void clear()
    {
        System.out.println("        Simulator reset\n");
        this.pc = 0;
        Arrays.fill(this.registers, 0);
        Arrays.fill(this.memory, 0);
    }
}
