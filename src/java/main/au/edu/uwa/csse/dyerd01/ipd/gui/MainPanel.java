// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

public class MainPanel extends JPanel implements ControlPanel.Externals
{
    private static final Logger logger = Logger.getLogger(MainPanel.class);
    
    private final ResultsPanel resultsPanel;
    
    public MainPanel()
    {
        super(new BorderLayout());
        add(new ControlPanel(this), BorderLayout.WEST);
        resultsPanel = new ResultsPanel();
        add(resultsPanel, BorderLayout.CENTER);
    }

    
    public void displayResults(RoundRobinResult[] results)
    {
        resultsPanel.setResults(results);
    }
    
    
    public void displayResults(EvolutionResult[] results)
    {
        logger.debug("Displaying " + results.length + " evolution results...");
        resultsPanel.setResults(results);
    }
}