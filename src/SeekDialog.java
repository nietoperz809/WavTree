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
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());

        final DefaultTableModel tm = new DefaultTableModel(new Object[]{"File", "Size", "Path"}, 0);
        table1.mySetModel(tm);
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
        // add your code here
        dispose();
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

        // for files
        if (arr[index].isFile())
        {
            File f = arr[index];
            String name = f.getName();
            String lower = name.toLowerCase();
            for (String s : Constants.SOUND_EXT)
            {
                if (lower.endsWith("."+s))
                {
                    tm.addRow(new Object[]
                            {
                                    name,
                                    "" + f.length(),
                                    f.getParent()+Constants.SEP
                            });
                    numFiles++;
                    infoText.setText ("Searching ... "+numFiles+" found");
                    break;
                }
            }
        }

        // for sub-directories
        else if (arr[index].isDirectory())
        {
            // recursion for sub-directories
            fillTable(arr[index].getAbsolutePath(),
                    arr[index].listFiles(),
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
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(panel2, BorderLayout.SOUTH);
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK);
        infoText = new JLabel();
        panel2.add (infoText);
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
