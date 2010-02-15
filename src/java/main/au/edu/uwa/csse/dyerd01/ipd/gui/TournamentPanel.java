// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Custom panel used for displaying the data (results or in-process data)
 * about a single tournament.
 * @author Daniel Dyer
 */
public class TournamentPanel extends JPanel
{
    public TournamentPanel()
    {
        super(new BorderLayout());
        add(createStatusPanel(), BorderLayout.NORTH);
        add(createDataPanel(), BorderLayout.CENTER);
    }
    
    
    private JComponent createStatusPanel()
    {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("Tournament Status"));
        return statusPanel;
    }
    
    
    private JComponent createDataPanel()
    {
        JTable dataTable = new JTable();
        JScrollPane scroller = new JScrollPane(dataTable);
        scroller.setBorder(BorderFactory.createTitledBorder("Tournament Output"));
        return scroller;
    }
}