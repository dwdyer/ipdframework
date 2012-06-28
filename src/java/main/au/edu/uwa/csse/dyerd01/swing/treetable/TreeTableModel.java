package au.edu.uwa.csse.dyerd01.swing.treetable;

import javax.swing.JTree;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

/**
 * Defines the methods that must be implemented by TreeTable data models.  Extends both
 * TreeModel and TableModel.
 * @author Daniel Dyer
 * @since 6/5/2004
 */
public interface TreeTableModel<T> extends TreeModel, TableModel
{
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    Object getValueAt(T node, int column);

    
    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     */
    boolean isCellEditable(T node, int column);

    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    void setValueAt(Object aValue, T node, int column);

    
    /**
     * Gets the total number of nodes in the tree (this will be more than the number of rows
     * in the table if the tree is not fully expanded).
     */
    int getSize();

    
    void setTree(JTree tree);

    
    /**
     * @return The model object displayed at the specified row.
     */
    T getObjectAtRow(int row);
}
