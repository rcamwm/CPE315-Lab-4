/*
Cameron McGiffert 
CPE315 Section 1
Lab 3
 */

import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MipsAssembler {
    public static Instruction[] getInstructions(String mipsFile)
    {
        Hashtable<String, Integer> labelToAddressTable = new Hashtable<>();
        int instructionCount = firstPass(mipsFile, labelToAddressTable);
        return secondPass(mipsFile, instructionCount, labelToAddressTable);
    } 

    private static int firstPass(String mipsFile, Hashtable<String, Integer> labelToAddressTable)
    {
        int instructionCount = 0;
        try
        {
            File source = new File(mipsFile);
            Scanner scan = new Scanner(source);
            
            while (scan.hasNextLine())
            {
                String instruction = scan.nextLine();
                instruction = stripComments(instruction);
                if (addLabelAddress(instruction, labelToAddressTable, instructionCount))
                    instruction = stripLabel(instruction);      
                if (!instruction.equals(""))
                    instructionCount++;
            }
            scan.close();      
        }
        catch (FileNotFoundException exception)
        {
            exception.printStackTrace();
        }
        return instructionCount;
    }

    private static Instruction[] secondPass(
        String mipsFile, int instructionCount, Hashtable<String, Integer> labelToAddressTable)
    {
        Instruction[] instructionList = new Instruction[instructionCount];
        try
        {
            File source = new File(mipsFile);
            Scanner scan = new Scanner(source);

            int i = 0;
            while (scan.hasNextLine())
            {
                String instruction = scan.nextLine();
                Instruction nextInstruction = parseInstruction(instruction, i + 1, labelToAddressTable);
                if (nextInstruction != null)
                {
                    instructionList[i] = nextInstruction;
                    i++;
                }
            }
            scan.close();            
        }
        catch (FileNotFoundException exception)
        {
            exception.printStackTrace();
        }
        return instructionList;
    }

    private static Instruction parseInstruction(
        String instruction, int instructionNumber, Hashtable<String, Integer> labelToAddressTable)
    {
        instruction = stripComments(instruction);
        instruction = stripLabel(instruction);
        if (instruction.equals(""))
            return null;

        int split = firstSplitIndex(instruction);
        String op = instruction.substring(0, split);
        char instructionType = Instruction.getInstructionType(op);
        if (instructionType == 0)
            Instruction.invalidInstructionError(op);

        String argString = instruction.substring(split);
        String[] args = argString.split(",");
        for (int i = 0; i < args.length; i++)
            args[i] = args[i].trim();

        if (instructionType == 'I')
            return createI_Format_Instruction(op, args, instructionNumber, labelToAddressTable);
        else if (instructionType == 'J')
            return createJ_Format_Instruction(op, args[0], labelToAddressTable);
        return createR_Format_Instruction(op, args);
    }

    private static Instruction_I createI_Format_Instruction(
        String operation, String[] args, int instructionNumber, Hashtable<String, Integer> labelToAddressTable)
    {
        int op = Operations.opTable.get(operation);
        int rt = Registers.registerTable.get(args[0]);
        int rs;
        int immediate;

        if (args.length == 2)
        {
            int leftP = args[1].indexOf("(");
            int rightP = args[1].indexOf(")");

            String s = args[1].substring(leftP + 1, rightP).trim();
            String i = args[1].substring(0, leftP).trim();

            rs = Registers.registerTable.get(s);
            if (i.equals(""))
                immediate = 0;
            else
                immediate = Integer.parseInt(i);
        }
        else
        {
            if (op == 4 || op == 5)
            {
                rs = rt;
                rt = Registers.registerTable.get(args[1]);
            }
            else
                rs = Registers.registerTable.get(args[1]);
                
            try
            {
                immediate = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException nfe)
            {
                int labelPosition = labelToAddressTable.get(args[2]);
                immediate = labelPosition - instructionNumber;
            }
        }
        return new Instruction_I(op, rt, rs, immediate);
    }

    private static Instruction_J createJ_Format_Instruction(
        String operation, String label, Hashtable<String, Integer> labelToAddressTable)
    {
        int op = Operations.opTable.get(operation);
        int address = labelToAddressTable.get(label);
        return new Instruction_J(op, address);
    }

    private static Instruction_R createR_Format_Instruction(String operation, String[] args)
    {
        int funct = Operations.functTable.get(operation);
        if (funct == 0 || funct == 2)
        {
            int rd = Registers.registerTable.get(args[0]);
            int rt = Registers.registerTable.get(args[1]);
            int shamt = Integer.parseInt(args[2]);
            if (funct == 0)
                return new Instruction_R('l', rd, rt, shamt);
            return new Instruction_R('r', rd, rt, shamt); 
        }
        else if (funct == 8)
        {
            int rs = Registers.registerTable.get(args[0]);
            return new Instruction_R(funct, rs);
        }
        int rd = Registers.registerTable.get(args[0]); 
        int rs = Registers.registerTable.get(args[1]);
        int rt = Registers.registerTable.get(args[2]);
        return new Instruction_R(funct, rd, rs, rt);
    }

    private static boolean addLabelAddress(
        String instruction, Hashtable<String, Integer> labelToAddressTable, int instructionCount)
    {
        int labelIndex = instruction.indexOf(':');
        if (labelIndex != -1)
        {
            String label = instruction.substring(0, labelIndex);
            labelToAddressTable.put(label, instructionCount);
            return true;
        }
        return false;
    }

    private static String stripComments(String line)
    {
        int commentIndex = line.indexOf('#');
        if (commentIndex != -1)
            line = line.substring(0, commentIndex);
        line = line.trim();
        return line;
    }

    private static String stripLabel(String line)
    {
        int labelIndex = line.indexOf(':');
        if (labelIndex != -1)
            line = line.substring(labelIndex + 1);
        line = line.trim();
        return line;
    }

    private static int firstSplitIndex(String instruction)
    {
        int w = getFirstWhiteSpace(instruction);
        int ds = instruction.indexOf("$");

        if (w == -1 && ds == -1)
            return -1;
        else if (w == -1)
            return ds;
        else if (ds == -1)
            return w;
        
        if (w < ds)
            return w;
        return ds;  
    }

    private static int getFirstWhiteSpace(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            if (Character.isWhitespace(s.charAt(i)))
                return i;
        }
        return -1;
    }
}
