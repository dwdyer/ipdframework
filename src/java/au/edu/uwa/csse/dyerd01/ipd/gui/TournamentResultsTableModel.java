// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import java.util.Arrays;
import javax.swing.table.*;
import net.sourceforge.anguish.sortabletable.*;
import net.sourceforge.anguish.treetable.AbstractTreeTableModel;
import net.sourceforge.anguish.treetable.TreeTableModel;

/**
 * @author Daniel Dyer
 */
public class TournamentResultsTableModel extends AbstractTreeTableModel implements SortableTableModel
{
    private static final String[] COLUMN_NAMES = new String[]{"Player", "Iterations", "Aggregate Pay-Off", "Aggregate Opponent Pay-Off", "Margin", "Av. Pay-Off"};
    private static final Class[] COLUMN_TYPES = new Class[]{TreeTableModel.class, Integer.class, Integer.class, Integer.class, Integer.class, Double.class};
    public static final int PLAYER_COLUMN = 0;
    public static final int ITERATIONS_COLUMN = 1;
    public static final int PAYOFF_COLUMN = 2;
    public static final int OPPONENT_PAYOFF_COLUMN = 3;
    public static final int MARGIN_COLUMN = 4;
    public static final int AVERAGE_PAYOFF_COLUMN = 5;
    
    private final RoundRobinResult[] data;
    private final RowComparator rowComparator = new RowComparator(this);
    
    public TournamentResultsTableModel(RoundRobinResult[] data)
    {
        super("Root");
        this.data = data;
    }
    
    
    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }
    
    
    public String getColumnName(int column)
    {
        return COLUMN_NAMES[column];
    }
    
    
    public Class getColumnClass(int column)
    {
        return COLUMN_TYPES[column];
    }
    
    
    public Object getValueAt(Object row, int column)
    {
        HeadToHeadResult record = (HeadToHeadResult) row;
        assert record != null : "Null record in table model.";
        switch (column)
        {
            case PLAYER_COLUMN:
            {
                if (record instanceof RoundRobinResult)
                {
                    return record.getPlayer().getName();
                }
                else
                {
                    return record.getOpponent().getName();
                }
            }
            case ITERATIONS_COLUMN:
            {
                return new Integer(record.getIterations());
            }
            case PAYOFF_COLUMN:
            {
                return new Integer(record.getAggregatePayOff());
            }
            case OPPONENT_PAYOFF_COLUMN:
            {
                return new Integer(record.getAggregateOpponentPayOff());
            }
            case MARGIN_COLUMN:
            {
                return new Integer(record.getMargin());
            }
            case AVERAGE_PAYOFF_COLUMN:
            {
                return new Double(record.getAveragePayOff());
            }
            default:
            {
                assert false : "Table model error.";
                return null;
            }
        }
    }
    
    
    public int getSize()
    {
        int size = data.length;
        for (int i = 0; i < data.length; i++)
        {
            size += data[i].getHeadToHeadResultCount();
        }
        return size;
    }
    
    
    public int getChildCount(Object parent)
    {
        if (parent == "Root")
        {
            return data.length;
        }
        else if (parent instanceof RoundRobinResult)
        {
            return ((RoundRobinResult) parent).getHeadToHeadResultCount();
        }
        return 0;
    }
    
    
    public Object getChild(Object parent, int index)
    {
        if (parent == "Root")
        {
            return data[index];
        }
        else if (parent instanceof RoundRobinResult)
        {
            return ((RoundRobinResult) parent).getHeadToHeadResult(index);
        }
        assert false : "Tree model error.";
        return null;
    }
    
    
    /**
     * @return The criteria currently used to sort this table.  Guaranteed to be non-null, returns
     * an empty array if the table is unsorted.
     */
    public SortCriterion[] getCurrentSortCriteria()
    {
        return rowComparator.getCriteria();
    }
    
    
    public boolean isColumnSortable(int column)
    {
        Class type = getColumnClass(column);
        return Comparable.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
    }
    
    
    /**
     * Sort the model and inform any listeners that the table data may have changed as a result.
     * @param criteria The parameters of the sort.
     * @see RowComparator
     */
    public void sort(SortCriterion[] criteria)
    {
        rowComparator.setCriteria(criteria);
        Arrays.sort(data, rowComparator);
        for (int i = 0; i < data.length; i++)
        {
            data[i].sortResults(rowComparator);
        }
        fireTreeStructureChanged(this, new Object[]{getRoot()}, null, null);
    }
}