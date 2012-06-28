// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import au.edu.uwa.csse.dyerd01.swing.sortabletable.SortCriterion;
import au.edu.uwa.csse.dyerd01.swing.treetable.AbstractTreeTableCellRenderer;
import au.edu.uwa.csse.dyerd01.swing.treetable.TreeTable;
import au.edu.uwa.csse.dyerd01.swing.treetable.TreeTableModel;

/**
 * @author Daniel Dyer
 */
public class TournamentResultsPanel extends JPanel
{
    private static final SortCriterion[] SORT_CRITERIA = {new SortCriterion(TournamentResultsTableModel.PAYOFF_COLUMN, false)};

    private TournamentResultsTableModel resultsTableModel = new TournamentResultsTableModel(new RoundRobinResult[0]);
    private final TreeTable resultsTable = new TreeTable(resultsTableModel, new TreeTableRenderer(resultsTableModel));
    
    public TournamentResultsPanel()
    {
        super(new BorderLayout());
        add(new JScrollPane(resultsTable));
        setBorder(BorderFactory.createTitledBorder("Tournament Results"));
        TournamentResultsTableRenderer renderer = new TournamentResultsTableRenderer();
        resultsTable.setDefaultRenderer(Integer.class, renderer);
        resultsTable.setDefaultRenderer(Double.class, renderer);
    }
    
    
    public void setResults(RoundRobinResult[] results)
    {
        resultsTableModel = new TournamentResultsTableModel(results);
        resultsTableModel.sort(SORT_CRITERIA);
        resultsTable.setModel(resultsTableModel);
    }
    
 
    private static final class TreeTableRenderer extends AbstractTreeTableCellRenderer
    {
        TreeTableRenderer(TreeTableModel model)
        {
            super(model);
        }

        
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column)
        {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setFont(model.getObjectAtRow(row) instanceof RoundRobinResult ? TournamentResultsTableRenderer.BOLD_FONT : TournamentResultsTableRenderer.PLAIN_FONT);
            visibleRow = row;
            return this;
        }
    }
}