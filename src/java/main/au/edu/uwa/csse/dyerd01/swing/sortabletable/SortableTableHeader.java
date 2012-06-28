package au.edu.uwa.csse.dyerd01.swing.sortabletable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Custom JTableHeader that supports sorting (when installed on a {@link JTable} that uses
 * a {@link SortableTableModel}).  This header can be installed on any JTable instance or
 * instance of a JTable subclass to make that table sortable, though sorting will only be
 * available if that table is using a sortable model.
 * @author Daniel Dyer
 * @since 27/4/2003
 * @see SortableTableModel
 * @see SortCriterion
 */
public class SortableTableHeader extends JTableHeader
{
    public SortableTableHeader()
    {
        init();
    }


    public SortableTableHeader(TableColumnModel columnModel)
    {
        super(columnModel);
        init();
    }


    /**
     * Helper method for constructors, sets up listener(s) on header.
     */
    private void init()
    {
        // Listener for sorting when the table header is clicked.
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent ev)
            {
                if (ev.getButton() == MouseEvent.BUTTON1) // Only sort on left-click.
                {
                    // Get a reference to the table model.
                    TableModel model = table.getModel();

                    if (model != null && model instanceof SortableTableModel) // Safety check to ensure table is sortable.
                    {
                        // Sort table by clicked on column.
                        int viewIndex = getTable().getColumnModel().getColumnIndexAtX(ev.getX());
                        int modelIndex = getColumnModel().getColumn(viewIndex).getModelIndex();

                        SortableTableModel sortableModel = (SortableTableModel) model;

                        if (sortableModel.isColumnSortable(modelIndex))
                        {
                            SortCriterion[] criteria = sortableModel.getCurrentSortCriteria();
                            boolean ascending = true;
                            if (criteria != null)
                            {
                                for (SortCriterion aCriteria : criteria)
                                {
                                    if (aCriteria.column == modelIndex)
                                    {
                                        ascending = !aCriteria.ascending;
                                        break;
                                    }
                                }
                            }
                            sortableModel.sort(new SortCriterion[] {new SortCriterion(modelIndex, ascending)});
                        }
                    }
                }
            }
        });
    }


    /**
     * Over-ridden to provide a renderer that will indicate which columns are
     * currently selected for sorting.
     */
    @Override
    protected TableCellRenderer createDefaultRenderer()
    {
        return new SortableTableHeaderCellRenderer();
    }
}