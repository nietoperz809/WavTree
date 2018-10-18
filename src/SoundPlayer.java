import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class SoundPlayer
{
    private static long runs;

    /**
     * Copy resource from jar to temp polder
     * @param name name of resource
     * @return Full path to extracted file
     * @throws IOException if smth gone wrong
     */
    static public String extractResource (String name) throws IOException
    {
        String tempName = System.getProperty("java.io.tmpdir")+name;
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

    public static void asyncPlay (String path)
    {
        final String arg = path;
        Constants.executor.submit((Callable<Void>) () ->
        {
            play(arg);
            return null;
        });
    }


    static synchronized public void play (String path)
    {
        try
        {
            ArrayList<String> args = new ArrayList<>();
            ProcessBuilder pb;
            if (runs >= 1)
            {
                runs = 0;
                args.add("taskkill"); // command name
                args.add("/F");
                args.add("/IM");
                args.add("ffplay.exe");
                pb = new ProcessBuilder(args);
                Process p1 = pb.start();
                Thread.sleep(500);
            }
            String exe = extractResource("ffplay.exe");
            args = new ArrayList<>();
            args.add (exe); // command name
            args.add (path);
            args.add ("-nodisp");
            args.add ("-autoexit");
            pb = new ProcessBuilder (args);
            pb.start();
            runs++;
        }
        catch (Exception e)
        {
            Util.showException(e);
            //System.out.println("failed "+e);
        }
    }

    static public String probe (String path)
    {
        try
        {
            String exe = extractResource("ffprobe.exe");
            ArrayList<String> args = new ArrayList<>();
            args.add (exe); // command name
            args.add (path);
            args.add ("-hide_banner");
            ProcessBuilder pb = new ProcessBuilder (args);
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
            return ("failed "+e);
        }
    }

    // Tester
    public static void main (String[] args) throws Exception
    {
        play ("c:\\music.wav");
        Thread.sleep(3000);
    }
}
