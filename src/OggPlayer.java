import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/*Git collas*/

public class OggPlayer
{
    static SourceDataLine line;
    static ExecutorService executor = Executors.newFixedThreadPool(10);

//    public static void main(String[] args)
//    {
//        player = new OggPlayer();
//        player.play(System.getProperty("user.dir")+ "/audiofile/001001.ogg");
//    }

    public static void asyncPlay (String path)
    {
        final String arg = path;
        executor.submit((Callable<Void>) () ->
        {
            play(arg);
            return null;
        });
    }

    public static void play(String filePath)
    {
        final File file = new File(filePath);

        try (final AudioInputStream in = getAudioInputStream(file))
        {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(outFormat);
            line.start();
            AudioInputStream inputMystream = AudioSystem.getAudioInputStream(outFormat, in);
            stream(inputMystream, line);

        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    private static AudioFormat getOutFormat(AudioFormat inFormat)
    {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    public static void stop()
    {
        if (line != null)
        {
            //line.drain();
            line.stop();
            line = null;
        }
    }

    private static void stream(AudioInputStream in, SourceDataLine line)
            throws IOException
    {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length))
        {
            line.write(buffer, 0, n);
        }
    }
}
