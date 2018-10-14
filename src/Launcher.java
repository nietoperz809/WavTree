class Launcher
{
    public static void main(String[] args)
    {
//        JOptionPane.showMessageDialog(null,
//                System.getProperty("user.dir"),
//                "InfoBox",
//                JOptionPane.INFORMATION_MESSAGE);

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