import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class SoundPlayer
{
    static private Process process;

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
            byte[] buff = new byte[1024];
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


    static public void play (String path)
    {
        if (process != null)
        {
            process.destroyForcibly();
            process = null;
        }
        try
        {
            String exe = extractResource("ffplay.exe");
            ArrayList<String> args = new ArrayList<>();
            args.add (exe); // command name
            args.add (path);
            args.add ("-nodisp");
            args.add ("-autoexit");
            ProcessBuilder pb = new ProcessBuilder (args);
            process = pb.start();
        }
        catch (IOException e)
        {
            System.out.println("failed "+e);
        }
    }

    // Tester
    public static void main (String[] args) throws Exception
    {
        play ("c:\\music.wav");
        Thread.sleep(3000);
    }
}
