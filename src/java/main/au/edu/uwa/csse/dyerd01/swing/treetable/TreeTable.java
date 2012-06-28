package au.edu.uwa.csse.dyerd01.swing.treetable;

import au.edu.uwa.csse.dyerd01.swing.sortabletable.SortableTable;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;


/**
 * Loosely based on the JTreeTable example on the Swing Connection
 * (http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html).
 * @author Daniel Dyer
 * @since 6/5/2004
 * @version $Revision: $
 */
public class TreeTable extends SortableTable
{
    /** A subclass of JTree. */
    protected AbstractTreeTableCellRenderer tree;


    public TreeTable(TreeTableModel treeTableModel, AbstractTreeTableCellRenderer tree)
    {
        init(treeTableModel, tree);
    }

    
    /**
     * Construct the tree table with a default renderer.
     */
    public TreeTable(TreeTableModel model)
    {
        init(model, new DefaultTreeTableCellRenderer(model));
    }

    
    /**
     * Helper method for constructor.
     */
    public final void init(TreeTableModel treeTableModel, AbstractTreeTableCellRenderer tree)
    {
        this.tree = tree;
        tree.setTreeTable(this);

        // Install a table model representing the visible rows in the tree.
        treeTableModel.setTree(tree);
        super.setModel(treeTableModel);

        // Force the JTable and JTree to share their row selection models.
        SelectionModelAdapter selectionAdapter = new SelectionModelAdapter();
        tree.setSelectionModel(selectionAdapter);
        setSelectionModel(selectionAdapter.getListSelectionModel());

        // Install the tree editor renderer and editor.
        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        // Update the tree row height to match that of the table.
        if (tree.getRowHeight() < 1)
        {
            // Metal looks better like this.
            setRowHeight(18);
        }
    }


    /**
     * Over-ridden to forward updateUI message to the tree.
     */
    @Override
    public void updateUI()
    {
        super.updateUI();
        if(tree != null)
        {
            tree.updateUI();
        }
        // Use tree colours for the table.
        LookAndFeel.installColorsAndFont(this,
                                         "Tree.background",
                                         "Tree.foreground",
                                         "Tree.font");
    }


    /**
     * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
     * paint the editor. The UI currently uses different techniques to
     * paint the renderers and editors and overriding setBounds() below
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     */
    @Override
    public int getEditingRow()
    {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
        editingRow;
    }

    
    /**
     * Overridden to pass the new rowHeight to the tree.
     */
    @Override
    public void setRowHeight(int rowHeight)
    {
        super.setRowHeight(rowHeight);
        if (tree != null)
        {
            tree.setRowHeight(getRowHeight());
        }
    }

    
    /**
     * Returns the tree that is being shared between the model.
     */
    public JTree getTree()
    {
        return tree;
    }


    public void setModel(TreeTableModel model)
    {
        tree.setModel(model);
        model.setTree(tree);
        super.setModel(model);
    }


    public void setRootVisible(boolean visible)
    {
        tree.setRootVisible(visible);
    }


    public void setTreeCellRenderer(TreeCellRenderer renderer)
    {
        tree.setCellRenderer(renderer);
    }

    
    /**
     * A TreeCellRenderer that displays a JTree.
     */
    private static class DefaultTreeTableCellRenderer extends AbstractTreeTableCellRenderer
    {
        DefaultTreeTableCellRenderer(TreeTableModel model)
        {
            super(model);
        }

        
        /**
         * TreeCellRenderer method. Overridden to update the visible row.
         */
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column)
        {
            if(isSelected)
            {
                setBackground(table.getSelectionBackground());
            }
            else
            {
                setBackground(table.getBackground());
            }
            visibleRow = row;
            return this;
        }
    }


    /**
     * TreeTableCellEditor implementation. Component returned is the
     * JTree.
     */
    public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor
    {
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column)
        {
            return tree;
        }

        /**
         * Overridden to return false, and if the event is a mouse event
         * it is forwarded to the tree.<p>
         * The behavior for this is debatable, and should really be offered
         * as a property. By returning false, all keyboard actions are
         * implemented in terms of the table. By returning true, the
         * tree would get a chance to do something with the keyboard
         * events. For the most part this is ok. But for certain keys,
         * such as left/right, the tree will expand/collapse where as
         * the table focus should really move to a different column. Page
         * up/down should also be implemented in terms of the table.
         * By returning false this also has the added benefit that clicking
         * outside of the bounds of the tree node, but still in the tree
         * column will select the row, whereas if this returned true
         * that wouldn't be the case.
         * <p>By returning false we are also enforcing the policy that
         * the tree will never be editable (at least by a key sequence).
         */
        @Override
        public boolean isCellEditable(EventObject ev)
        {
            if (ev instanceof MouseEvent)
            {
                for (int counter = getColumnCount() - 1; counter >= 0; counter--)
                {
                    if (getColumnClass(counter) == TreeTableModel.class)
                    {
                        MouseEvent mouseEvent = (MouseEvent) ev;
                        MouseEvent newMouseEvent = new MouseEvent(tree,
                                                                  mouseEvent.getID(),
                                                                  mouseEvent.getWhen(),
                                                                  mouseEvent.getModifiers(),
                                                                  mouseEvent.getX() - getCellRect(0, counter, true).x,
                                                                  mouseEvent.getY(),
                                                                  mouseEvent.getClickCount(),
                                                                  mouseEvent.isPopupTrigger());
                        tree.dispatchEvent(newMouseEvent);
                        break;
                    }
                }
            }
            return false;
        }

        
        public Object getCellEditorValue()
        {
            return null;
        }
    }


    /**
     * Extends DefaultTreeSelectionModel and maintains its ListSelectionModel for use by the
     * JTable.  Listens to changes in the table selection model in order to keep the tree
     * selection model consistent.
     */
    private class SelectionModelAdapter extends DefaultTreeSelectionModel implements ListSelectionListener
    {
        /** Set to true when we are updating the ListSelectionModel. */
        protected boolean updatingListSelectionModel;

        SelectionModelAdapter()
        {
            getListSelectionModel().addListSelectionListener(this);
        }

        
        /**
         * Returns the list selection model. SelectionModelAdapter
         * listens for changes to this model and updates the selected paths
         * accordingly.
         */
        public final ListSelectionModel getListSelectionModel()
        {
            return listSelectionModel;
        }
        

        /**
         * Called when the table selection changes so that the tree selection can be updated.
         */
        public void valueChanged(ListSelectionEvent ev)
        {
            updateSelectedPathsFromSelectedRows();
        }

        
        /**
         * This is overridden to set <code>updatingListSelectionModel</code>
         * and message super. This is the only place DefaultTreeSelectionModel
         * alters the ListSelectionModel.
         */
        @Override
        public void resetRowSelection()
        {
            if(!updatingListSelectionModel)
            {
                updatingListSelectionModel = true;
                try
                {
                    super.resetRowSelection();
                }
                finally
                {
                    updatingListSelectionModel = false;
                }
            }
        }

        
        /**
         * If <code>updatingListSelectionModel</code> is false, this will
         * reset the selected paths from the selected rows in the list
         * selection model.
         */
        protected void updateSelectedPathsFromSelectedRows()
        {
            if(!updatingListSelectionModel)
            {
                updatingListSelectionModel = true;
                try
                {
                    int minSelection = listSelectionModel.getMinSelectionIndex();
                    int maxSelection = listSelectionModel.getMaxSelectionIndex();
                    clearSelection();
                    if(minSelection >= 0 && maxSelection >= 0)
                    {
                        for(int i = minSelection; i <= maxSelection; i++)
                        {
                            if(listSelectionModel.isSelectedIndex(i))
                            {
                                TreePath selectionPath = tree.getPathForRow(i);
                                if(selectionPath != null)
                                {
                                    addSelectionPath(selectionPath);
                                }
                            }
                        }
                    }
                }
                finally
                {
                    updatingListSelectionModel = false;
                }
            }
        }
    }
}