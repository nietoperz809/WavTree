import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTransferHandler extends TransferHandler
{
    WTMain _mainwnd;

    public FileTransferHandler (WTMain wtm)
    {
        _mainwnd = wtm;
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        JXTable jx = (JXTable)c;
        int i = jx.convertRowIndexToModel(jx.getSelectedRow());
        TableModel tm = jx.getModel();
        String filename = (String) tm.getValueAt(i, 0);
        String path = _mainwnd.topLabel.getText() + filename;

        List<File> files = new ArrayList<>();
        files.add(new File(path));
        return new FileTransferable(files);
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return MOVE;
    }
}

class FileTransferable implements Transferable
{

    private List<File> files;

    public FileTransferable(List<File> files) {
        this.files = files;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.javaFileListFlavor);
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
    {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return files;
    }
}