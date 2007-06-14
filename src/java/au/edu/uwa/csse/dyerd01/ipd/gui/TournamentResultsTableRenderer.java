// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.sourceforge.anguish.sortabletable.SortableTableModel;

public class TournamentResultsTableRenderer extends DefaultTableCellRenderer
{
    static final Font PLAIN_FONT = new Font("Dialog", Font.PLAIN, 12);
    static final Font BOLD_FONT = new Font("Dialog", Font.BOLD, 12);
    private static final Color NEGATIVE_COLOR = new Color(204, 0, 0);
    private static final Color POSITIVE_COLOR = new Color(0, 204, 0);
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.00 %");
    
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        Object valueObject = value;
        if (column == TournamentResultsTableModel.AVERAGE_PAYOFF_COLUMN)
        {
            valueObject = DOUBLE_FORMAT.format(((Double) valueObject).doubleValue());
        }
        Component renderer = super.getTableCellRendererComponent(table, valueObject, isSelected, hasFocus, row, column);
        SortableTableModel model = (SortableTableModel) table.getModel();
        renderer.setFont(model.getObjectAtRow(row) instanceof RoundRobinResult ? BOLD_FONT : PLAIN_FONT);
        if (value instanceof Number)
        {
            ((JLabel) renderer).setHorizontalAlignment(JLabel.RIGHT);
        }
        
        if (column == TournamentResultsTableModel.MARGIN_COLUMN)
        {
            renderer.setForeground((Integer) value < 0 ? NEGATIVE_COLOR : POSITIVE_COLOR);
        }
        else
        {
            renderer.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        }
        return renderer;
    }
}
