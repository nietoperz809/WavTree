import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirLister
{
    public static DefaultTableModel popTable (String rootPath)
    {
        List<String> columns = new ArrayList<String>();
        List<String[]> values = new ArrayList<String[]>();

        columns.add("File");
        columns.add("Size");

        File actual = new File(rootPath);
        for (File f : Objects.requireNonNull(actual.listFiles()))
        {
            if (f.isFile())
            {
                values.add (new String[]{f.getName(), ""+f.length()});
            }
        }
        return new DefaultTableModel (values.toArray(new Object[][] {}),
                                        columns.toArray());
    }

    public static DefaultTreeModel getRootEntry (String _root)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(_root);
        DefaultTreeModel tm = new DefaultTreeModel(root);
        getNodeEntry(tm, root, _root);
        return tm;
    }

    public static void getNodeEntry (DefaultTreeModel tm,
                                     DefaultMutableTreeNode rootNode,
                                     String rootPath)
    {
        rootNode.removeAllChildren();
        File actual = new File(rootPath);
        for (File f : Objects.requireNonNull(actual.listFiles()))
        {
            if (f.isDirectory())
            {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName() + "\\");
                tm.insertNodeInto(child, rootNode, rootNode.getChildCount());
            }
        }
        tm.reload(rootNode);
    }

}
