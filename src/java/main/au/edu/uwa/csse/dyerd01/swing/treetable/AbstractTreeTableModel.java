package au.edu.uwa.csse.dyerd01.swing.treetable;

import java.util.EventListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * @author Daniel Dyer
 * @since 6/5/2004
 */
public abstract class AbstractTreeTableModel<T> extends AbstractTableModel implements TreeTableModel<T>
{
    private JTree tree;
    private Object root;
    protected final EventListenerList listenerList = new EventListenerList();

    protected AbstractTreeTableModel(Object root)
    {
        setRoot(root);
    }


    protected AbstractTreeTableModel()
    {
        // Do nothing, for convenience in sub-class.
    }

    
    public Object getRoot()
    {
        return root;
    }

    
    protected final void setRoot(Object root)
    {
        this.root = root;
    }

    
    public boolean isLeaf(Object node)
    {
        return getChildCount(node) == 0;
    }

    
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // TO DO: Not sure if this is needed.
    }

    
    /**
     * This is not called in the JTree's default mode: use a naive implementation.
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        for (int i = 0; i < getChildCount(parent); i++)
        {
            if (getChild(parent, i).equals(child))
            {
                return i;
            }
        }
        return -1;
    }

    
    public void addTreeModelListener(TreeModelListener listener)
    {
        listenerList.add(TreeModelListener.class, listener);
    }

    
    public void removeTreeModelListener(TreeModelListener listener)
    {
        listenerList.remove(TreeModelListener.class, listener);
    }

    
    protected void fireTreeNodesChanged(Object source,
                                        Object[] path,
                                        int[] childIndices,
                                        Object[] children)
    {
        EventListener[] listeners = listenerList.getListeners(TreeModelListener.class);
        TreeModelEvent event = null;
        for (EventListener listener : listeners)
        {
            // Lazily create the event.
            if (event == null)
            {
                event = new TreeModelEvent(source, path, childIndices, children);
            }
            ((TreeModelListener) listener).treeNodesChanged(event);
        }
        delayedFireTableDataChanged();
    }

    
    protected void fireTreeNodesInserted(Object source,
                                         Object[] path,
                                         int[] childIndices,
                                         Object[] children)
    {
        EventListener[] listeners = listenerList.getListeners(TreeModelListener.class);
        TreeModelEvent event = null;
        for (EventListener listener : listeners)
        {
            // Lazily create the event.
            if (event == null)
            {
                event = new TreeModelEvent(source, path, childIndices, children);
            }
            ((TreeModelListener) listener).treeNodesInserted(event);
        }
        delayedFireTableDataChanged();
    }

    
    protected void fireTreeNodesRemoved(Object source,
                                        Object[] path,
                                        int[] childIndices,
                                        Object[] children)
    {
        EventListener[] listeners = listenerList.getListeners(TreeModelListener.class);
        TreeModelEvent event = null;
        for (EventListener listener : listeners)
        {
            // Lazily create the event.
            if (event == null)
            {
                event = new TreeModelEvent(source, path, childIndices, children);
            }
            ((TreeModelListener) listener).treeNodesRemoved(event);
        }
        delayedFireTableDataChanged();
    }


    protected void fireTreeStructureChanged(Object source,
                                            Object[] path,
                                            int[] childIndices,
                                            Object[] children)
    {
        EventListener[] listeners = listenerList.getListeners(TreeModelListener.class);
        TreeModelEvent event = null;
        for (EventListener listener : listeners)
        {
            // Lazily create the event.
            if (event == null)
            {
                event = new TreeModelEvent(source, path, childIndices, children);
            }
            ((TreeModelListener) listener).treeStructureChanged(event);
        }
        delayedFireTableDataChanged();
    }

    
    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed.
     */
    protected void delayedFireTableDataChanged()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                fireTableDataChanged();
            }
        });
    }

    
   /**
    * By default make the column with the Tree in it the only editable one.
    * Making this column editable causes the JTable to forward mouse
    * and keyboard events to the JTree.
    */
    public boolean isCellEditable(T node, int column)
    {
         return getColumnClass(column) == TreeTableModel.class;
    }

    
    /**
     * Does nothing, over-ride in sub-class to create editable model.
     */
    public void setValueAt(Object aValue, T node, int column)
    {
        // Deliberately empty, default is non-editable model, over-ride in sub-class.
    }


    public int getRowCount()
    {
        return tree.getRowCount();
    }


    @SuppressWarnings("unchecked")
    public T getObjectAtRow(int row)
    {
        TreePath treePath = tree.getPathForRow(row);
        return (T) treePath.getLastPathComponent();
    }

    
    public Object getValueAt(int row, int column)
    {
        return getValueAt(getObjectAtRow(row), column);
    }

    
    @Override
    public boolean isCellEditable(int row, int column)
    {
         return isCellEditable(getObjectAtRow(row), column);
    }

    
    @Override
    public void setValueAt(Object value, int row, int column)
    {
        setValueAt(value, getObjectAtRow(row), column);
    }


    public void setTree(JTree tree)
    {
        this.tree = tree;

        tree.addTreeExpansionListener(new TreeExpansionListener()
        {
            public void treeExpanded(TreeExpansionEvent event)
            {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event)
            {
                fireTableDataChanged();
            }
        });
    }
}
