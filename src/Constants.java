import java.io.File;

interface Constants
{
    String SEP = File.separator;
    String PATH_TO_STOREDIR = System.getProperty("user.dir") + SEP + "WavStore";
    String PATH_TO_CONFIGFILE = System.getProperty("user.dir") + SEP + "wavvy_settings.txt";
    String DEFAULT_ROOT = "c:" + SEP;
}
