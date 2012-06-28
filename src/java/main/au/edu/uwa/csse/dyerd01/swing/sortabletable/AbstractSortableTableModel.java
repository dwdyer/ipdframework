package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * <p>Convenience base class for sortable table models.  The model is backed by an {@link ArrayList}.
 * If you require a sortable model that is backed by some other object you should not use
 * this class and instead extend {@link AbstractTableModel} and implement {@link SortableTableModel}
 * directly, using a {@link RowComparator} or equivalent for sorting.</p>
 *
 * <p>As a minimum, sub-classes must implement the {@link #getColumnClass(int)},
 * {@link #getColumnCount()} and {@link #getValueAt(Object,int)} methods.  For an editable model
 * {@link #setValueAt(Object,int,int)} must also be over-ridden, taking care to maintain the general
 * contract of the {@link #getCurrentSortCriteria()} method.</p>
 * @param <T> The row type (each row is an object of this type).
 * @see RowComparator
 * @see #getColumnCount()
 * @see #getColumnClass(int)
 * @see #getValueAt(Object,int)
 * @see #setValueAt(Object,int,int)
 * @see #getCurrentSortCriteria()
 * @author Daniel Dyer
 * @since 27/4/2003
 */
public abstract class AbstractSortableTableModel<T> extends AbstractTableModel implements SortableTableModel<T>
{
    /** A list of objects, each representing a single row in the model. */
    protected final List<T> rows;

    /** The comparator used in sorting. */
    protected final RowComparator<T> rowComparator = new RowComparator<T>(this);


    /**
     * Default constructor, creates an empty model.
     */
    protected AbstractSortableTableModel()
    {
        this.rows = new ArrayList<T>();
    }


    /**
     * Initialise the model with the specified data (each item in the collection represents one
     * row in the model).
     */
    protected AbstractSortableTableModel(Collection<T> rows)
    {
        this.rows = new ArrayList<T>(rows);
        sort(getCurrentSortCriteria());
    }


    /**
     * Initialise the model with the specified data (each element in the array represents one
     * row in the model).
     */
    protected AbstractSortableTableModel(T[] rows)
    {
        this(Arrays.asList(rows));
    }


    /**
     * @return The number of rows in the model (which is the number of elements in the
     * {@link ArrayList} that backs this model).
     */
    public int getRowCount()
    {
        return rows.size();
    }


    /**
     * Sort the model and inform any listeners that the table data may have changed as a result.
     * @param criteria The parameters of the sort.
     * @see RowComparator
     */
    public final void sort(SortCriterion[] criteria)
    {
        rowComparator.setCriteria(criteria);
        Collections.sort(rows, rowComparator);
        fireTableDataChanged();
    }


    /**
     * @return The criteria currently used to sort this table.  Guaranteed to be non-null, returns
     * an empty array if the table is unsorted.
     */
    public final SortCriterion[] getCurrentSortCriteria()
    {
        return rowComparator.getCriteria();
    }


    /**
     * Returns the object that encapsulates the row data for the row in this model identified
     * by the specified index.
     * @return The object at the specified row index.
     */
    public T getObjectAtRow(int row)
    {
        return rows.get(row);
    }


    /**
     * This method delegates to {@link #getValueAt(Object,int)} and cannot be overridden.  This
     * method is declared as final since the only valid implementation in a sortable table model
     * is to delegate to the {@link #getValueAt(Object,int)} method declared by the
     * {@link SortableTableModel} interface.
     * @param row The index of the row in the model in which the cell resides.
     * @param column The index of the column in the model in which the cell resides.
     * @return The value at the cell identified by the arguments.
     * @see #getValueAt(Object,int)
     */
    public final Object getValueAt(int row, int column)
    {
        return getValueAt(getObjectAtRow(row), column);
    }


    /**
     * This default implementation returns true if the type of the column, as returned by
     * {@link #getColumnClass(int)}, implements {@link Comparable} or is {@link Boolean Boolean.class},
     * since these are the only classes that can be sorted by the {@link RowComparator} instance used
     * to sort this table model.
     * This should be sufficient for most purposes, unless you wish to explicitly prevent sorting
     * on a column which would otherwise be sortable, in which case you must override this method.
     * You should also over-ride this method if you wish to be able to sort on columns with types
     * that do not implement {@link Comparable} (or are not {@link Boolean Boolean.class}) and
     * you have overridden the sort and {@link #getCurrentSortCriteria()} methods to enable this.
     * @param column The index of the column in the model for which the enquiry is being made.
     * @return <code>true</code> if the column is sortable, <code>false</code> otherwise.
     * @see #getColumnClass(int)
     * @see Comparable
     */
    public boolean isColumnSortable(int column)
    {
        Class<?> type = getColumnClass(column);
        return Comparable.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
    }


    /**
     * Add the row represented by <code>row</code> to the model.  Inserts the row in the appropriate
     * position to maintain the correct ordering of rows as specified by the current sort criteria.
     * If there are no current criteria the new row is added at the bottom of the table.
     * @param row The object that encapsulates the row data to be added to the model.
     */
    public void addRow(T row)
    {
        int index;
        if (getCurrentSortCriteria().length == 0)
        {
            index = getRowCount();
        }
        else
        {
            index = Collections.binarySearch(rows, row, rowComparator);
            if (index < 0)
            {
                index = Math.abs(index + 1);
            }
        }
        rows.add(index, row);
        fireTableRowsInserted(index, index);
    }


    /**
     * Removes the specified row from the model if it is present.
     * @param row The object representation of the row that is to be removed from the model.
     */
    public void removeRow(T row)
    {
        int index;
        // If the list is unsorted we'll have to use brute force to find the object.
        if (getCurrentSortCriteria().length == 0)
        {
            index = rows.indexOf(row);
        }
        // Otherwise we can use a more efficient binary search.
        else
        {
            index = Collections.binarySearch(rows, row, rowComparator);
        }
        if (index >= 0)
        {
            removeRow(index);
        }
    }


    /**
     * Removes the row at the specified index in the model.
     * @param index The index of the row in the model that is to be removed.
     */
    public void removeRow(int index)
    {
        rows.remove(index);
        fireTableRowsDeleted(index, index);
    }


    /**
     * <p>The default implementation of <code>getColumnClass(int)</code> inherited from
     * {@link AbstractTableModel} is not suitable for use in a {@link SortableTableModel}
     * as it always returns <code>Object.class</code>.  While this is sufficient to satisfy
     * the compiler and to avoid run-time exceptions it serves no useful purpose in the
     * context of an <code>AbstractSortableTableModel</code> since none of the columns
     * would be sortable (because of the quite reasonable behaviour of the default
     * implementation of the {@link #isColumnSortable(int)} method) and the model
     * degenerates into a standard table model in terms of functionality.</p>
     *
     * <p>For the reasons outlined above the concrete method inherited from the superclass
     * has been over-riden with an abstract version, effectively forcing the programmer
     * to reimplement this method in a way that satisfies their particular requirements.
     * Unfortunately there is no generally useful implementation of this method, which
     * is why it has not been over-ridden with an alternative concrete implementation.</p>
     *
     * <p>Whilst over-riding a concrete method implementation with an abstract version is
     * not desirable from a theoretical OO perspective (since implementation inheritence
     * should not be used to remove capabilities), it was decided to sacrifice ideology
     * in favour of practical considerations in this instance.  The alternative would have
     * been to not inherit from {@link AbstractTableModel} and instead duplicate the method
     * implementations that it provides (with the exception of the 
     * <code>getColumnClass(int)</code> method).</p>
     * @see #isColumnSortable(int)
     */
    @Override
    public abstract Class<?> getColumnClass(int column);
}