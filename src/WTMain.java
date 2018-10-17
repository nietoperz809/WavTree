import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

class WTMain implements TransferInfo
{
    private JPanel mainPanel;
    private JTree tree1;
    private MyJTable table1;
    public JLabel topLabel;
    private JButton copyPathButton;
    private JButton editButton;
    private JCheckBox onlySoundCheck;

    private String lastPath = Constants.DEFAULT_ROOT;
    private String audacityPath = "audacity.exe";

    private final HexView hexView = new HexView(/*dummy*/);
    private String storeDir = Constants.PATH_TO_STOREDIR;

    private String editDir = "notepad.exe";
    private JButton pathUpButton;

    /**
     * Tree path to string
     *
     * @param tp TreePath object
     * @return String representation (Components separated by \)
     */
    private String TPtoString (TreePath tp)
    {
        StringBuilder tempSpot = new StringBuilder();

        for (int counter = 0, maxCounter = tp.getPathCount(); counter < maxCounter;
             counter++)
        {
            tempSpot.append(tp.getPathComponent(counter));
        }
        return tempSpot.toString();
    }

    private void tree1MouseClicked (MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON3) // Right click
        {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Seek for sound files");
            item.addActionListener(e1 ->
            {
                TreePath tp = tree1.getPathForLocation(e.getX(), e.getY());
                if (tp != null)
                {
                    //System.out.println(TPtoString(tp));
                    SeekDialog.create ("SeekSounds", TPtoString(tp));
                }

            });
            menu.add(item);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void table1MouseClicked (MouseEvent e)
    {
        int i = table1.convertRowIndexToModel(table1.getSelectedRow());
        TableModel tm = table1.getModel();
        String filename = (String) tm.getValueAt(i, 0);
        String path = topLabel.getText() + filename;

        if (e.getButton() == MouseEvent.BUTTON3) // Right click
        {
            JPopupMenu menu = new JPopupMenu();

            // Copy Path
            JMenuItem item = new JMenuItem("Copy full path");
            item.addActionListener(e1 ->
            {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(path), null);
            });
            menu.add(item);

            // Open with Audacity
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
                    Util.showException(e2);
                }
            });
            menu.add(item);

            // Copy to Store
            item = new JMenuItem("Copy to store");
            item.addActionListener(e1 ->
            {
                String dest = storeDir+Constants.SEP+filename;
                try
                {
                    Files.copy (new File(path).toPath(),
                            new File(dest).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e2)
                {
                    Util.showException(e2);
                }
            });
            menu.add(item);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
        else
        {
            hexView.readfirstBytes(path, 64);
        }
    }

    private WTMain ()
    {
        new File(storeDir).mkdirs();
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

            lastPath = TPtoString(tp);

            System.out.println(lastPath);

            DirLister.getNodeEntry(tm, node, lastPath);
            tree1.expandPath(tp);

            updateTable(lastPath);
        });

        // Enable tree drop target
        new DropTarget(tree1, new DropTargetAdapter()
        {
            @Override
            public void drop (DropTargetDropEvent event)
            {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors)
                {
                    try
                    {
                        if (flavor.isFlavorJavaFileListType())
                        {
                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
                            String path = files.get(0).getParent();
                            updateUI(path+Constants.SEP);
                            // System.out.printf(f.getParent());
                            return; // only one file
                        }
                    }
                    catch (Exception e)
                    {
                        Util.showException(e);
                    }
                }
            }
        });

        // Table click listener
        table1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked (MouseEvent e)
            {
                table1MouseClicked(e);
            }
        });

        // Tree click listener
        tree1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked (MouseEvent e)
            {
                tree1MouseClicked(e);
            }
        });

        // Copy Path to clipboard
        copyPathButton.addActionListener(e ->
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(topLabel.getText()), null);
        });
        // Start CFG editor
        editButton.addActionListener(e ->
        {
            ArrayList<String> args = new ArrayList<>();
            args.add (editDir); // command name
            args.add (Constants.PATH_TO_CONFIGFILE); // optional args added as separate list items
            ProcessBuilder pb = new ProcessBuilder (args);
            try
            {
                pb.start();
            }
            catch (IOException e2)
            {
                Util.showException(e2);
            }
        });
        // Checkbox clicked
        onlySoundCheck.addActionListener(e -> updateTable(lastPath));
        // Path up button
        pathUpButton.addActionListener(e ->
        {
            String par = new File(lastPath).getParent();
            if (par != null)
            {
                if (!par.endsWith(Constants.SEP))
                    par += Constants.SEP;
                lastPath = par;
                updateUI (lastPath);
            }
            else
            {
                System.out.println("no parent");
            }
        });
    }

    private void updateTable (String path)
    {
        DefaultTableModel tab = DirLister.getFilledTableModel(path, onlySoundCheck.isSelected());
        table1.setModel2(tab);
        topLabel.setText(path);
    }

    private void updateUI (String path)
    {
        DefaultTreeModel tm = DirLister.getRootEntry(path);
        tree1.setModel(tm);
        updateTable(path);
    }

    private void ExecConfig()
    {
        try
        {
            ConfigFile conf = new ConfigFile(Constants.PATH_TO_CONFIGFILE);
            conf.setAction("root", strings ->
                    lastPath = strings[0]);
            conf.setAction("audacity", strings ->
                    audacityPath = strings[0]);
            conf.setAction("store", strings ->
                    storeDir = strings[0]);
            conf.setAction("edit", strings ->
                    editDir = strings[0]);
            conf.execute();
        }
        catch (IOException e)
        {
            System.out.println("no config file");
            //Util.showException(e);
        }
        updateUI(lastPath);
    }

    public static void main (String[] args)
    {
        JFrame frame = new JFrame("WTMain");
        WTMain wt = new WTMain();

        wt.ExecConfig();

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
        splitPane1.setRightComponent(hexView);

        mainPanel.add(splitPane1, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        sp3.setLeftComponent(scrollPane1);
        tree1 = new JTree();
        scrollPane1.setViewportView(tree1);
        final JScrollPane scrollPane2 = new JScrollPane();
        sp3.setRightComponent(scrollPane2);
        table1 = new MyJTable(this);
        table1.setDragEnabled(true);
        table1.setTransferHandler(new FileTransferHandler(this));
        scrollPane2.setViewportView(table1);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        final JPanel panelBottom = new JPanel();
        //panelBottom.setLayout();
        mainPanel.add(panel1, BorderLayout.NORTH);
        mainPanel.add(panelBottom, BorderLayout.SOUTH);
        topLabel = new JLabel();
        panel1.add(topLabel);
        pathUpButton = new JButton ("Up");
        panel1.add (pathUpButton);
        copyPathButton = new JButton("Copy path");
        onlySoundCheck = new JCheckBox("Only sounds");
        editButton = new JButton("CfG File");
        panelBottom.add(editButton);
        panelBottom.add(copyPathButton);
        panelBottom.add(onlySoundCheck);
    }

    @Override
    public String getPath (int rowNumber)
    {
        return topLabel.getText();
    }
}

