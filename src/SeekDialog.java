import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeekDialog extends JDialog implements TransferInfo
{
    private int numFiles;
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel infoText;
    private MyJTable table1;
    private AtomicBoolean running = new AtomicBoolean();

    public SeekDialog (String title, String root)
    {
        setupUI();
        setTitle(title);
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());

        final DefaultTableModel tm = new DefaultTableModel(new Object[]{"File", "Size", "Path"}, 0);
        table1.setModel2(tm);
        Constants.executor.submit((Callable<Void>) () ->
        {
            infoText.setText ("Searching ...");
            numFiles = 0;
            running.set(true);
            File arr[] = new File(root).listFiles();
            fillTable(root, arr, 0, tm);
            infoText.setText ("Ready, found "+numFiles+" samplings");
            return null;
        });
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing (WindowEvent e)
            {
                System.out.println("closing");
                running.set(false);
            }
        });
    }

    private void onOK ()
    {
        running.set(false);
    }

    public static SeekDialog create (String title, String path)
    {
        SeekDialog dialog = new SeekDialog(title, path);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    void fillTable (String dir, File[] arr, int index, DefaultTableModel tm)
    {
        // terminate condition
        if (index == arr.length || !running.get())
        {
            System.out.println("leave filltable " + running);
            return;
        }

        File fil = arr[index];

        // for files
        if (fil.isFile())
        {
            String name = fil.getName();
            String lower = name.toLowerCase();
            for (String s : Constants.DOTTED_SOUND_EXT)
            {
                if (lower.endsWith(s))
                {
                    tm.addRow(new Object[]
                            {
                                    name,
                                    "" + fil.length(),
                                    fil.getParent()+Constants.SEP
                            });
                    numFiles++;
                    infoText.setText ("Searching ... "+numFiles+" found");
                    break;
                }
            }
        }

        // for sub-directories
        else if (fil.isDirectory())
        {
            infoText.setText ("Searching in    "+fil.getAbsolutePath());
            // recursion for sub-directories
            fillTable(fil.getAbsolutePath(),
                    fil.listFiles(),
                    0, tm);
        }

        // recursion for main directory
        fillTable(dir, arr, ++index, tm);
    }


    private void setupUI ()
    {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.SOUTH);
        buttonOK = new JButton();
        buttonOK.setText("Stop search");
        panel2.add(buttonOK, BorderLayout.WEST);
        infoText = new JLabel();
        panel2.add (infoText, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, BorderLayout.CENTER);
        table1 = new MyJTable(this);
        scrollPane1.setViewportView(table1);
    }

    @Override
    public String getPath (int rowNumber)
    {
        TableModel tm = table1.getModel();
        String path = (String) tm.getValueAt(rowNumber, 2);
        return path;
    }
}
