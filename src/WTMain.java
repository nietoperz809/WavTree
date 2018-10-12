import hexeditor.HexView;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.sort.TableSortController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class WTMain
{
    private JPanel mainPanel;
    private JTree tree1;
    private JXTable table1;
    private JLabel topLabel;
    private JButton copyPathButton;

    private String root;
    private String audacityPath = "audacity.exe";

    //private int[] dummy = new int[256];
    private HexView hexView = new HexView(/*dummy*/);
    //private final JScrollPane hexViewScrollPane = new JScrollPane();

    private MediaPlayer lastClip;

    final static JFXPanel fxPanel = new JFXPanel(); // start JFX

    /**
     * Tree path to string
     *
     * @param tp TreePath object
     * @return String representation (Components separated by \)
     */
    public String TPtoString (TreePath tp)
    {
        StringBuffer tempSpot = new StringBuffer();

        for (int counter = 0, maxCounter = tp.getPathCount(); counter < maxCounter;
             counter++)
        {
            tempSpot.append(tp.getPathComponent(counter));
        }
        return tempSpot.toString();
    }

    public WTMain ()
    {
        // Build UI
        setupUI();
        // set Colors of JTree
        tree1.setBackground(Color.black);
        TreeCellRenderer cr = tree1.getCellRenderer();
        if (cr instanceof DefaultTreeCellRenderer)
        {
            DefaultTreeCellRenderer dtcr =
                    (DefaultTreeCellRenderer) cr;

            // Set the various colors
            dtcr.setBackgroundNonSelectionColor(Color.black);
            dtcr.setBackgroundSelectionColor(Color.gray);
            dtcr.setTextSelectionColor(Color.white);
            dtcr.setTextNonSelectionColor(Color.green);

            // Finally, set the tree's background color
            tree1.setBackground(Color.black);
        }
        // Tree selection listener
        tree1.addTreeSelectionListener(e ->
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree1.getLastSelectedPathComponent();
            DefaultTreeModel tm = (DefaultTreeModel) tree1.getModel();
            if (node == null)
            {
                return;
            }
            TreePath tp = e.getPath();

            String path = TPtoString(tp);

            System.out.println(path);

            DirLister.getNodeEntry(tm, node, path);
            tree1.expandPath(tp);

            updateTable(path);
        });
        // Table click listener
        table1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked (MouseEvent e)
            {
                int i = table1.convertRowIndexToModel(table1.getSelectedRow());
                TableModel tm = table1.getModel();
                String path = topLabel.getText() + (String) tm.getValueAt(i, 0);

                if (e.getButton() == MouseEvent.BUTTON3) // Right click
                {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem("Copy full path");
                    item.addActionListener(e1 ->
                    {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(path), null);
                    });
                    menu.add(item);

                    item = new JMenuItem("Open with Audacity");
                    item.addActionListener(e1 ->
                    {
                        ArrayList<String> args = new ArrayList<>();
                        args.add (audacityPath); // command name
                        args.add (path); // optional args added as separate list items
                        ProcessBuilder pb = new ProcessBuilder (args);
                        try
                        {
                            Process p = pb.start();
                        }
                        catch (IOException e2)
                        {
                            System.out.println("cannot start audacity");
                        }
                    });
                    menu.add(item);

                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
                else
                {
                    hexView.readfirstBytes(path, 64);
                    //hexViewScrollPane.getVerticalScrollBar().setVisible(false);
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
                        new OggPlayer().play(path);
                    }
                }
            }
        });
        // Copy Path to clipboard
        copyPathButton.addActionListener(e ->
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(topLabel.getText()), null);
        });
    }

    public void playWav (String filename)
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

    private void updateTable (String path)
    {
        DefaultTableModel tab = DirLister.popTable(path);
        table1.setModel(tab);
        topLabel.setText(path);
        // Set table long comparator for size column
        TableSortController con = (TableSortController) table1.getRowSorter();
        con.setComparator(1, (Comparator<String>) (o1, o2) ->
                (int)Math.signum(Long.parseLong(o1) - Long.parseLong(o2)));
    }

    private void updateUI (String path)
    {
        DefaultTreeModel tm = DirLister.getRootEntry(path);
        tree1.setModel(tm);
        updateTable(path);
    }

    public static void main (String[] args)
    {
        JFrame frame = new JFrame("WTMain");
        WTMain wt = new WTMain();

        try
        {
            ConfigFile conf = new ConfigFile("wavvy_settings.txt");
            conf.setAction("root", strings ->
            {
                wt.root = strings[0];
            });
            conf.setAction("audacity", strings ->
            {
                wt.audacityPath = strings[0];
            });
            conf.execute();
        }
        catch (IOException e)
        {
            wt.root = "c:\\";
            System.out.println("config file error");
        }
        wt.updateUI(wt.root);

        frame.setContentPane(wt.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void setupUI ()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);

        final JSplitPane sp3 = new JSplitPane();
        splitPane1.setLeftComponent(sp3);

        hexView.setBackground(new java.awt.Color(0, 102, 102));
        hexView.setFont(new java.awt.Font("Lucida Console", 1, 10)); // NOI18N
        hexView.setForeground(new java.awt.Color(255, 255, 102));
        hexView.setPreferredSize(new Dimension(100,80));
        //hexView.setColumns(10);
        //hexView.setRows(2);
//        hexViewScrollPane.setViewportView(hexView);
//        splitPane1.setRightComponent(hexViewScrollPane);
        splitPane1.setRightComponent(hexView);


        mainPanel.add(splitPane1, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        sp3.setLeftComponent(scrollPane1);
        tree1 = new JTree();
        scrollPane1.setViewportView(tree1);
        final JScrollPane scrollPane2 = new JScrollPane();
        sp3.setRightComponent(scrollPane2);
        table1 = new JXTable();
        scrollPane2.setViewportView(table1);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        mainPanel.add(panel1, BorderLayout.NORTH);
        topLabel = new JLabel();
        panel1.add(topLabel);
        copyPathButton = new JButton();
        copyPathButton.setText("Copy path");
        panel1.add(copyPathButton);
    }


    public JComponent $$$getRootComponent$$$ ()
    {
        return mainPanel;
    }
}

