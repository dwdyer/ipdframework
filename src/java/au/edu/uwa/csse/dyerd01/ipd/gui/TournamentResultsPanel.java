// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreeModel;
import net.sourceforge.anguish.sortabletable.*;
import net.sourceforge.anguish.treetable.*;

/**
 * @author Daniel Dyer
 */
public class TournamentResultsPanel extends JPanel
{
    private static final SortCriterion[] SORT_CRITERIA = new SortCriterion[]{new SortCriterion(TournamentResultsTableModel.PAYOFF_COLUMN, false)};

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
        public TreeTableRenderer(TreeTableModel model)
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