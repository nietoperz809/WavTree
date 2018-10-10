import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Objects;

public class DirLister
{
    public static DefaultTreeModel doIt (String _root)
    {
        File actual = new File(_root);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(_root);
        DefaultTreeModel tm = new DefaultTreeModel(root);
        for (File f : actual.listFiles())
        {
            if (f.isDirectory())
            {
                DefaultMutableTreeNode subroot = new DefaultMutableTreeNode(f.getName() + "\\");
                tm.insertNodeInto(subroot, root, root.getChildCount());
            }
        }
        return tm;
    }

    public static DefaultMutableTreeNode updNode (DefaultTreeModel tm,
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
        return rootNode;
    }
}
