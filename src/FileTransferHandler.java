import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileTransferHandler extends TransferHandler
{
    private final TransferInfo _info;

    public FileTransferHandler (TransferInfo info)
    {
        _info = info;
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        MyJTable jx = (MyJTable)c;
        int i = jx.convertRowIndexToModel(jx.getSelectedRow());
        TableModel tm = jx.getModel();
        String filename = (String) tm.getValueAt(i, 0);
        String path = _info.getPath(i)+filename; //_mainwnd.topLabel.getText() + filename;

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

    private final List<File> files;

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
            throws UnsupportedFlavorException
    {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return files;
    }
}