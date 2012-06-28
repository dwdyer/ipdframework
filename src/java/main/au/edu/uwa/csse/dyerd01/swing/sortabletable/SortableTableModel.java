package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import javax.swing.table.TableModel;

/**
 * Extension of the standard {@link TableModel} interface to provide facilities
 * for sorting rows based on the values in one or more columns.  A sortable
 * table model can be used in conjunction with a standard {@link javax.swing.JTable} to provide
 * a table with rows that can be sorted programatically.  In order to facilitate
 * user-driven sorting of rows, the JTable must also have a {@link SortableTableHeader}
 * installed.
 * @author Daniel Dyer
 * @since 27/4/2003
 * @see SortCriterion
 * @see SortableTableHeader
 */
public interface SortableTableModel<T> extends TableModel
{
    /**
     * Sort the model by the specified column(s).  The order of the columns in the array is
     * the order of priority for the sort (i.e. the first element in the array is the most
     * significant criterion).
     */
    void sort(SortCriterion[] columns);


    /**
     * <p>Retrieves the current sort criteria for the model.  This method must never return null.
     * If there are no criteria it should return an empty array.</p>
     *
     * <p>The general contract of this method is that the order of rows in the model MUST ALWAYS
     * be consistent with the sort criteria returned by this method.  This has implications for
     * editable models (those that implement {@link #setValueAt(Object,int,int)})
     * since changing a value in a column that is part of the sort criteria MAY make the order of
     * the rows inconsistent with the sort criteria.  The programmer must deal with this possibility
     * by either re-sorting the model or by reseting the current sort criteria (i.e. make this method
     * return an empty array after such a change).</p>
     * @return The criteria by which the model is currently sorted as an array with one element
     * for each column in the sort, in order of priority.  The array may be empty, but never null.
     */
    SortCriterion[] getCurrentSortCriteria();


    /**
     * Get the object that represents the single specified row.
     * This method makes the assumption that each row is modelled by a distinct object.
     * @return The object that encapsulates the data for all of the cells in the specified
     * row.
     */
    T getObjectAtRow(int row);


    /**
     * Get the value at the specified column from the row represented by the first parameter.
     * This method makes the assumption that each row is modelled by a distinct object.
     * Typically this method would be implemented with a switch statement that returns the value
     * obtained by calling the appropriate accessor on the <code>row</code> argument for the
     * specified column index.
     * @return The value of the cell in the specified column for the specified row.
     * @see #getValueAt(int,int)
     */
    Object getValueAt(T row, int column);


    /**
     * Determines whether a particular column is sortable or not.
     * @return <code>true</code> if the column can be used as a criterion for sorting,
     * <code>false</code> otherwise.
     */
    boolean isColumnSortable(int column);
}