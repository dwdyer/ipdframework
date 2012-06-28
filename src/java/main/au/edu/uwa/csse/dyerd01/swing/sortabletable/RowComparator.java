package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import java.util.Comparator;

/**
 * Comparator for sorting a table model based on some user defined criteria.  Supports
 * sorting of any object that implements {@link Comparable}.  Also supports sorting of
 * {@link Boolean} objects even though the <code>Boolean</code> class doesn't implement
 * <code>Comparable</code>, because <code>Boolean</code>s are immutable and cannot be
 * sub-classed to implement <code>Comparable</code>.  <code>Booleans</code> are sorted
 * in the following (ascending) order: <code>null</code>, {@link Boolean#FALSE},
 * {@link Boolean#TRUE}.
 * @param <T> The row type (each row is an object of this type).
 * @author Daniel Dyer
 * @since 27/4/2003
 * @see SortCriterion
 * @see SortableTableModel
 * @see Comparable
 */
public class RowComparator<T> implements Comparator<T>
{
    /** Integer object constant representing boolean false. */
    private static final Integer FALSE = 0;
    /** Integer object constant representing boolean true. */
    private static final Integer TRUE = 1;

    /** Reference to the table model for which rows will be compared. */
    protected final SortableTableModel<T> model;

    /** The criteria for the comparisons. */
    protected SortCriterion[] criteria = new SortCriterion[0];


    /**
     * Creates a comparator that compares rows in the specified model.
     * @param model The {@link SortableTableModel} that the values to be compared will come from.
     */
    public RowComparator(SortableTableModel<T> model)
    {
        this.model = model;
    }


    /**
     * Sets the criteria for comparisons.
     * @param criteria The criteria for comparisons.  The first element in the array
     * is the most significant criterion.
     */
    public void setCriteria(SortCriterion[] criteria)
    {
        this.criteria = criteria;
    }


    /**
     * Sets the criteria for comparisons.
     * @return The criteria for comparisons.  The first element in the array is the most
     * significant criterion.
     */
    public SortCriterion[] getCriteria()
    {
        return criteria;
    }


    public int compare(T obj1, T obj2)
    {
        int compare = 0;
        int colIndex = 0;
        while (compare == 0 && criteria != null && colIndex < criteria.length)
        {
            Comparable value1;
            Comparable value2;
            // Special case for Boolean objects, which are not directly comparable (do not implement the
            // Comparable interface).
            if (model.getColumnClass(criteria[colIndex].column).equals(Boolean.class))
            {
                value1 = booleanAsInteger((Boolean) model.getValueAt(obj1, criteria[colIndex].column));
                value2 = booleanAsInteger((Boolean) model.getValueAt(obj2, criteria[colIndex].column));
            }
            else
            {
                value1 = (Comparable) model.getValueAt(obj1, criteria[colIndex].column);
                value2 = (Comparable) model.getValueAt(obj2, criteria[colIndex].column);
            }
            // Swap the values if this is a descending sort.
            if (!criteria[colIndex].ascending)
            {
                Comparable<?> temp = value1;
                value1 = value2;
                value2 = temp;
            }

            if (value1 == null && value2 == null)
            {
                compare = 0;
            }
            else if (value1 != null && value2 == null)
            {
                compare = 1;
            }
            else if (value1 == null && value2 != null)
            {
                compare = -1;
            }
            else
            {
                compare = value1.compareTo(value2);
            }
            colIndex++;
        }
        return compare;
    }


    /**
     * Helper method to convert a {@link Boolean} object to an {@link Integer} (C-style,
     * 0 is false, 1 is true) because the <code>Boolean</code> class does not implement
     * {@link Comparable} and cannot be sub-classed.
     * @return An <code>Integer</code> object that represents the <code>Boolean</code>
     * argument (0 for false and 1 for true).  Returns <code>null</code> if the argument is null.
     */
    private Integer booleanAsInteger(Boolean b)
    {
        if (b == null)
        {
            return null;
        }
        return b ? TRUE : FALSE;
    }
}
