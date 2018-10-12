
public class Launcher
{
    public static void main(String[] args)
    {
        JarClassLoader jcl = new JarClassLoader();
        try
        {
            jcl.invokeMain("WTMain", args);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    } // main()

}