package au.edu.uwa.csse.dyerd01.swing.sortabletable;

/**
 * Models a single criterion (a column index and a boolean ascending/descending flag) for sorting a
 * table model.  Immutable.
 * @author Daniel Dyer
 * @since 27/4/2003
 * @see SortableTableModel
 */
public final class SortCriterion
{
    /** Index of the table column to sort on. */
    public final int column;

    /** Flag indicating whether the sort should be ascending or descending. */
    public final boolean ascending;

    /**
     * Constructor, sets immutable fields.
     */
    public SortCriterion(int column, boolean ascending)
    {
        this.column = column;
        this.ascending = ascending;
    }
}
