/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author Administrator
 */
public class Util
{
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void showException (Exception ex)
    {
        showMessageDialog(null, ex, "Something's gone wrong", ERROR_MESSAGE);
    }

    public enum OS_TYPE {LINUX, WINDOWS, SOLARIS, MACOS, UNKNOWN};
    private static String osNameMatch = System.getProperty("os.name").toLowerCase();

    public static OS_TYPE getOS()
    {
        if(osNameMatch.contains("linux")) 
        {
            return OS_TYPE.LINUX;
        }
        if(osNameMatch.contains("windows"))
        {
           return OS_TYPE.WINDOWS;
        }
        if(osNameMatch.contains("solaris") || osNameMatch.contains("sunos"))
        {
           return OS_TYPE.SOLARIS;
        }
        if(osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin"))
        {
            return OS_TYPE.MACOS;
        }
        return OS_TYPE.UNKNOWN;
    }

    public static void killWindowsTask (String name)
    {
        if (getOS() != OS_TYPE.WINDOWS)
            return;
        ArrayList<String> args = new ArrayList<>();
        ProcessBuilder pb;
        args.add("taskkill"); // command name
        args.add("/F");
        args.add("/IM");
        args.add(name);
        try
        {
            Process pro = new ProcessBuilder(args).start();
            pro.waitFor();
        }
        catch (Exception e)
        {
            showException(e);
        }
    }

    public static int getHexIndex(char c)
    {
        for (int n = 0; n < digits.length; n++)
        {
            if (Character.toUpperCase(c) == Character.toUpperCase(digits[n]))
            {
                return n;
            }
        }
        return -1;
    }

    public static String toHex8(int in)
    {
        String sb = String.valueOf(digits[(in >>> 4) & 15]) +
                digits[in & 15];
        return sb;
    }
   
    public static String toHex16(int in)
    {
        String sb = toHex8(in >> 8) + toHex8(in);
        return sb;
    }
    
//    private static int readHex (String in) throws Exception
//    {
//        try
//        {
//            return Integer.parseInt(in.trim(), 16);
//        }
//        catch (Exception ex)
//        {
//            throw new Exception ("Not a hex value");
//        }
//    }
    
//    private static int readNumber (String in) throws Exception
//    {
//        if (in.charAt(0) == '$')
//            return readHex (in.substring(1));
//        else if (in.charAt(0) == '\'' && in.charAt(2) == '\'')
//            return (in.charAt(1));
//        return Integer.parseInt(in);
//    }

}
