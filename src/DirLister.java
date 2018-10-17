import org.apache.commons.io.FileUtils;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DirLister
{
    public static DefaultTableModel getFilledTableModel (String rootPath, boolean onlySounds)
    {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String[]> values = new ArrayList<>();

        columns.add("File");
        columns.add("Size");

        List<File> files = (List<File>)
                FileUtils.listFiles(new File(rootPath),
                        onlySounds ? Constants.SOUND_EXT : null,
                        false);
        try
        {
            for (File f : files)
            {
                if (f.isFile())
                {
                    values.add (new String[]{f.getName(), ""+f.length()});
                }
            }
        }
        catch (Exception e)
        {
            Util.showException(e);
        }
        return new DefaultTableModel (values.toArray(new Object[][] {}),
                                        columns.toArray());
    }

//    public static DefaultTableModel getEmptySearchTableModel (String rootPath)
//    {
//        ArrayList<String> columns = new ArrayList<>();
//        ArrayList<String[]> values = new ArrayList<>();
//
//        columns.add("File");
//        columns.add("Size");
//        columns.add("Path");
//
////        List<File> files = (List<File>)
////                FileUtils.listFiles(new File(rootPath),
////                        Constants.SOUND_EXT,
////                        true);
////        try
////        {
////            for (File f : files)
////            {
////                if (f.isFile())
////                {
////                    values.add (new String[]{f.getName(), ""+f.length(), f.getPath()});
////                }
////            }
////        }
////        catch (Exception e)
////        {
////            System.out.println("tabModelFill fail");
////        }
//        return new DefaultTableModel (values.toArray(new Object[][] {}),
//                columns.toArray());
//    }


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
        try
        {
            rootNode.removeAllChildren();
            File actual = new File(rootPath);
            for (File f : Objects.requireNonNull(actual.listFiles()))
            {
                if (f.isDirectory())
                {
                    DefaultMutableTreeNode child = 
                            new DefaultMutableTreeNode(f.getName() + Constants.SEP);
                    tm.insertNodeInto(child, rootNode, rootNode.getChildCount());
                }
            }
            tm.reload(rootNode);
        }
        catch (Exception e)
        {
            Util.showException(e);
        }
    }

}
