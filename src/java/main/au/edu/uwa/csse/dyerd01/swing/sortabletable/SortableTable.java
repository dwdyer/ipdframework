package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Convenience class to provide a sortable JTable.  This class is not strictly necessary
 * since any JTable can be made sortable by installing a {@link SortableTableHeader} (though
 * the sorting functionality will only be available when the table is using a sortable model),
 * but it is provided as a convenient replacement for the standard {@link JTable} class.
 * Equivalents are provided for all of the super-class constructors. When a non-sortable
 * model is used it behaves exactly like the standard JTable.
 * @author Daniel Dyer
 * @since 11/10/2003
 */
public class SortableTable extends JTable
{
    public SortableTable()
    {
    }


    public SortableTable(TableModel dm)
    {
        super(dm);
    }

    
    public SortableTable(TableModel dm, TableColumnModel cm)
    {
        super(dm, cm);
    }

    
    public SortableTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
    {
        super(dm, cm, sm);
    }


    public SortableTable(int numRows, int numColumns)
    {
        super(numRows, numColumns);
    }

    
    public SortableTable(Vector rowData, Vector columnNames)
    {
        super(rowData, columnNames);
    }

    
    public SortableTable(Object[][] rowData, Object[] columnNames)
    {
        super(rowData, columnNames);
    }

    
    /**
     * Over-ridden to use a sortable table header by default.
     */
    @Override
    protected JTableHeader createDefaultTableHeader()
    {
        return new SortableTableHeader(columnModel);
    }
}