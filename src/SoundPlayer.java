import java.io.*;
import java.util.ArrayList;

public class SoundPlayer
{
    private static Process process;

//    public static void asyncPlay (String path)
//    {
//        play(path);
//
////        final String arg = path;
////        Constants.executor.submit((Callable<Void>) () ->
////        {
////            play(arg);
////            return null;
////        });
//    }

    static public void play (String path)
    {
        try
        {
            if (Util.getOS() != Util.OS_TYPE.WINDOWS)
            {
                throw new Exception("Not implemented on non-windows");
            }
            if (process != null && process.isAlive())
            {
                Util.killWindowsTask(Constants.WINDOWSPLAYER);
                System.out.println("kill");
            }
            String exe = extractResource(Constants.WINDOWSPLAYER);
            ArrayList<String> args = new ArrayList<>();
            args.add(exe); // command name
            args.add(path);
            args.add("-nodisp");
            args.add("-autoexit");
            process = new ProcessBuilder(args).start();
            System.out.println(process);
        }
        catch (Exception e)
        {
            Util.showException(e);
        }
    }

    /**
     * Copy resource from jar to temp polder
     *
     * @param name name of resource
     * @return Full path to extracted file
     * @throws IOException if smth gone wrong
     */
    static public String extractResource (String name) throws IOException
    {
        String tempName = System.getProperty("java.io.tmpdir") + name;
        if (!new File(tempName).exists())
        {
            InputStream inStream = ClassLoader.getSystemResourceAsStream(name);
            OutputStream os = new FileOutputStream(tempName);
            byte[] buff = new byte[8192];
            for (; ; )
            {
                int r = inStream.read(buff);
                if (r == -1)
                {
                    break;
                }
                os.write(buff, 0, r);
            }
            inStream.close();
            os.close();
        }
        return tempName;
    }

    static public String probe (String path)
    {
        try
        {
            String exe = extractResource("ffprobe.exe");
            ArrayList<String> args = new ArrayList<>();
            args.add(exe); // command name
            args.add(path);
            args.add("-hide_banner");
            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            StringBuilder sb = new StringBuilder("<html>");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null)
            {
                sb.append(s.trim()).append("<br>");
            }
            while ((s = stdError.readLine()) != null)
            {
                sb.append(s.trim()).append("<br>");
            }
            sb.append("</html>");
            return sb.toString();
        }
        catch (IOException e)
        {
            return ("failed " + e);
        }
    }

    // Tester
    public static void main (String[] args) throws Exception
    {
        play("c:\\music.wav");
        Thread.sleep(3000);
    }
}
