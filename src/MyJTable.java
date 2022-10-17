import javafx.embed.swing.JFXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.sort.TableSortController;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

public class MyJTable extends JXTable
{
    final static JFXPanel fxPanel = new JFXPanel(); // start JFX

    private TransferInfo m_in;

    @Override
    public String getToolTipText (MouseEvent event)
    {
        int rp = rowAtPoint(event.getPoint());
        if (rp == -1)
            return null;
        TableModel tm = getModel();
        int rowNum = convertRowIndexToModel(rp);
        String filename = (String) tm.getValueAt(rowNum, 0);
        String path = m_in.getPath(rowNum) + filename;

        return SoundPlayer.probe(path);
    }

    public MyJTable (TransferInfo in)
    {
        super();
        m_in = in;
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
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
                            || lower.endsWith(".ogg")
                            || lower.endsWith(".au")
                            || lower.endsWith(".aiff")
                    )
                    {
                        SoundPlayer.play(path);
                    }
                }
            }
        });
    }

    public void setModel2 (TableModel dataModel)
    {
        setModel(dataModel);
        // Set table long comparator for size column
        TableSortController con = (TableSortController) this.getRowSorter();
        con.setComparator(1, (Comparator<String>) (o1, o2) ->
                (int)Math.signum(Long.parseLong(o1) - Long.parseLong(o2)));
    }
}
