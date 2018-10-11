/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

interface LoadFunc
{
    Object doIt (File file, FileReader reader);
}

interface SaveFunc
{
    void doIt (FileOutputStream fo);
}

/**
 *
 * @author Administrator
 */
public class FileUtility
{
    private static Object load(String s1, String s2, Component parent, LoadFunc code) throws Exception
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText("Get it!");
        chooser.setFileFilter(new FileNameExtensionFilter(s1, s2));
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
        {
            String name = chooser.getSelectedFile().getAbsolutePath();
            File file = new File(name); //for ex foo.txt
            FileReader reader = new FileReader(file);
            return code.doIt(file, reader);
        }
        return null;
    }

    private static void save(String s1, String s2, Component parent, SaveFunc code) throws Exception
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText("Save now!");
        chooser.setFileFilter(new FileNameExtensionFilter(s1, s2));
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
        {
            String name = chooser.getSelectedFile().getAbsolutePath();
            code.doIt(new FileOutputStream(name));
        }
    }

    /**
     *
     * @param parent
     * @return
     * @throws Exception
     */
    public static String loadSource(Component parent) throws Exception
    {
        return (String)load("6502 ASM code", "asm", parent, (File file, FileReader reader) ->
        {
            char[] chars = new char[(int) file.length()];
            try
            {
                reader.read(chars);
            }
            catch (IOException ex)
            {

            }
            return new String(chars);
        });
    }

    /**
     *
     * @param parent
     * @param content
     * @throws Exception
     */
    public static void saveSource(Component parent, String content) throws Exception
    {
        save("6502 ASM code", "asm", parent, (FileOutputStream fo) ->
        {
            PrintStream out = new PrintStream(fo);
            out.print(content);
        });
    }

    public static void saveMem(Component parent, int[] ints) throws Exception
    {
        save("Memory Block", "raw", parent, (FileOutputStream fo) ->
        {
            DataOutputStream dd = new DataOutputStream (fo);
            for (int i : ints)
            {
                try
                {
                    dd.writeInt(i);
                }
                catch (IOException ex)
                {
                }
            }
        });
    }
    
    public static void loadMem(Component parent, int[] mem) throws Exception
    {
        int[] x = (int[]) load("Memory block", "raw", parent, (File file, FileReader reader) ->
        {
            try
            {
                DataInputStream di = new DataInputStream (new FileInputStream(file));
                int len = (int)file.length()/4;
                int[] ints = new int[len];
                for (int i=0; i<len; i++)
                    ints[i] = di.readInt();
                di.close();
                return ints;
            }
            catch (Exception ex)
            {
            }
            return null;
        });
        System.arraycopy(x, 0, mem, 0, mem.length);
    }
}
