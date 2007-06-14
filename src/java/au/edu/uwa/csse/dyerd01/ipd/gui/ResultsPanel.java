// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.*;
import javax.swing.*;

/**
 * @author Daniel Dyer
 */
public class ResultsPanel extends JPanel
{
    private final TournamentResultsPanel tournamentResultsPanel = new TournamentResultsPanel();
    private final EvolutionResultsPanel evolutionResultsPanel = new EvolutionResultsPanel();
    
    public ResultsPanel()
    {
        super(new CardLayout());
        add(tournamentResultsPanel, "Tournament");
        add(evolutionResultsPanel, "Evolution");
    }
    
    
    public void setResults(RoundRobinResult[] results)
    {
        tournamentResultsPanel.setResults(results);
        ((CardLayout) getLayout()).show(this, "Tournament");
    }
    
    
    public void setResults(EvolutionResult[] results)
    {
        evolutionResultsPanel.setResults(results);
        ((CardLayout) getLayout()).show(this, "Evolution");
    }
}