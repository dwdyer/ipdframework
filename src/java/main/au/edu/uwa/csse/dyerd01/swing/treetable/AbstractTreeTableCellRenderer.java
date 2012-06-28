package au.edu.uwa.csse.dyerd01.swing.treetable;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * Base class for tree table renderers.
 * @author Daniel Dyer
 * @since 6/5/2004
 */
public abstract class AbstractTreeTableCellRenderer extends JTree implements TableCellRenderer
{
    /** Last table/tree row asked to renderer. */
    protected int visibleRow;
    protected TreeTable treeTable;
    protected TreeTableModel model;

    protected AbstractTreeTableCellRenderer(TreeTableModel model)
    {
        super(model);
        this.model = model;
        setRootVisible(false);
        setShowsRootHandles(true);
    }


    public void setTreeTable(TreeTable treeTable)
    {
        this.treeTable = treeTable;
    }

    
    /**
     * Sets the row height of the tree, and forwards the row height to
     * the table.
     */
    @Override
    public void setRowHeight(int rowHeight)
    {
        if (rowHeight > 0)
        {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight)
            {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }


    /**
     * This is overridden to set the height to match that of the JTable.
     */
    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        if (treeTable != null)
        {
            super.setBounds(x, 0, w, treeTable.getHeight());
        }
    }


    /**
     * Sub-classed to translate the graphics such that the last visible
     * row will be drawn at 0,0.
     */
    @Override
    public void paint(Graphics g)
    {
        g.translate(0, -visibleRow * getRowHeight());
        super.paint(g);
    }


    private TreePath[] getExpandedPaths()
    {
        Enumeration<TreePath> pathEnum = getExpandedDescendants(new TreePath(getModel().getRoot()));
        List<TreePath> list = new ArrayList<TreePath>();
        while (pathEnum != null && pathEnum.hasMoreElements())
        {
            list.add(pathEnum.nextElement());
        }
        return list.toArray(new TreePath[list.size()]);
    }


    private void setExpandedPaths(TreePath[] paths)
    {
        for (TreePath path : paths)
        {
            setExpandedState(path, true);
        }
    }


    @Override
    protected TreeModelListener createTreeModelListener()
    {
        return new TreeModelHandler()
        {
            @Override
            public void treeStructureChanged(TreeModelEvent ev)
            {
                TreePath[] paths = getExpandedPaths();
                super.treeStructureChanged(ev);
                setExpandedPaths(paths);
            }
        };
    }
    
    
    /**
     * Over-ridden to set the colours of the renderer to match the table.
     * Over-ride in sub-class if this is not the desired behaviour.
     */
    @Override
    public void updateUI()
    {
        super.updateUI();
        TreeCellRenderer renderer = getCellRenderer();
        if (renderer instanceof DefaultTreeCellRenderer)
        {
            DefaultTreeCellRenderer treeRenderer = ((DefaultTreeCellRenderer) renderer);
            treeRenderer.setBorderSelectionColor(null);
            treeRenderer.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
            treeRenderer.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
        }
    }
}
