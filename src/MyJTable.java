import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.sort.TableSortController;

import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Comparator;

public class MyJTable extends JXTable
{
    private MediaPlayer lastClip;
    final static JFXPanel fxPanel = new JFXPanel(); // start JFX


    public MyJTable (TransferInfo in)
    {
        super();
        setDragEnabled(true);
        setTransferHandler(new FileTransferHandler(in));

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked (MouseEvent e)
            {
                int i = convertRowIndexToModel(getSelectedRow());
                TableModel tm = getModel();
                String filename = (String) tm.getValueAt(i, 0);
                String path = in.getPath(i) + filename;
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    String lower = path.toLowerCase();
                    if (lower.endsWith(".wav")
                            || lower.endsWith(".mp3")
                            || lower.endsWith(".aac")
                            || lower.endsWith(".pcm")
                    )
                    {
                        playWav(path);
                    }
                    else if (lower.endsWith(".ogg")
                            || lower.endsWith(".au")
                            || lower.endsWith(".aiff")

                    )
                    {
                        OggPlayer.stop();
                        OggPlayer.asyncPlay(path);
                    }
                }
            }
        });
    }

    private void playWav (String filename)
    {
        if (lastClip != null)
        {
            lastClip.stop();
        }
        Media hit = new Media(new File(filename).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        lastClip = mediaPlayer;
        mediaPlayer.play();
    }

    public void mySetModel (TableModel dataModel)
    {
        setModel(dataModel);
        // Set table long comparator for size column
        TableSortController con = (TableSortController) this.getRowSorter();
        con.setComparator(1, (Comparator<String>) (o1, o2) ->
                (int)Math.signum(Long.parseLong(o1) - Long.parseLong(o2)));
    }
}
