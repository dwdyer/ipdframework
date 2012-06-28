package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * A table header renderer for use with the {@link SortableTableHeader} class to indicate the current
 * sort status of a {@link JTable} that uses a sortable model.
 * @author Daniel Dyer
 * @since 30/4/2003
 * @see SortableTableHeader
 */
class SortableTableHeaderCellRenderer extends DefaultTableCellRenderer
{
    private static final ImageIcon DESCENDING_ICON =
        new ImageIcon(ClassLoader.getSystemResource("au/edu/uwa/csse/dyerd01/swing/sortabletable/descending.gif"));
    
    private static final ImageIcon ASCENDING_ICON =
        new ImageIcon(ClassLoader.getSystemResource("au/edu/uwa/csse/dyerd01/swing/sortabletable/ascending.gif"));

    /**
     * Default constructor, intialises renderer with UI defaults and sets alignment to be
     * left justified to accommodate the ascending/descending sort icon on the right.
     */
    SortableTableHeaderCellRenderer()
    {
        setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"),
                                                     BorderFactory.createEmptyBorder(0, 2, 0, 2))); // Compound border so that border isn't drawn over text.
        setHorizontalAlignment(LEFT);
        setHorizontalTextPosition(LEADING);
    }


    /**
     * Renders a table header cell.  Modified copy of default renderer used by
     * {@link JTableHeader}.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        // Set colours.
        if (table != null)
        {
            JTableHeader header = table.getTableHeader();
            if (header != null)
            {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }

        // Set text.
        setText(value == null ? "" : value.toString());

        // Convert view column index to model index.
        int modelColumnIndex = table.getColumnModel().getColumn(column).getModelIndex();

        // Set icon.
        setIcon(null);
        TableModel model = table.getModel();
        if (model instanceof SortableTableModel) // Safety check in case this renderer is installed on a non-sortable table.
        {
            SortCriterion[] sortColumns = ((SortableTableModel) model).getCurrentSortCriteria(); // Guaranteed to be non-null.
            for (SortCriterion sortColumn : sortColumns)
            {
                if (sortColumn.column == modelColumnIndex)
                {
                    setIcon(sortColumn.ascending ? ASCENDING_ICON : DESCENDING_ICON);
                    break;
                }
            }
        }
        return this;
    }
}