import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface Constants
{
    String SEP = File.separator;
    String PATH_TO_STOREDIR = System.getProperty("user.dir") + SEP + "WavStore";
    String PATH_TO_CONFIGFILE = System.getProperty("user.dir") + SEP + "wavvy_settings.txt";
    String DEFAULT_ROOT = "c:" + SEP;

    String[] SOUND_EXT = new String[] { "wav", "mp3", "aac", "pcm", "ogg", "au", "aiff" };
    String[] DOTTED_SOUND_EXT = Arrays.stream(SOUND_EXT).map(e -> "."+e).toArray(String[]::new);

    String WINDOWSPLAYER = "ffplay.exe";

    ExecutorService executor = Executors.newFixedThreadPool(10);
}
