// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.*;
import javax.swing.*;

/**
 * @author Daniel Dyer
 */
public class EvolutionResultsPanel extends JPanel
{
    private final EvolutionAveragesChartPanel averages = new EvolutionAveragesChartPanel();
    private final PopulationCompositionChartPanel composition = new PopulationCompositionChartPanel();
    private final FixedPlayerAdvantageChartPanel advantage = new FixedPlayerAdvantageChartPanel();
    private final FixedPlayerRankChartPanel rank = new FixedPlayerRankChartPanel();
    private final CoEvolutionChartPanel coevo = new CoEvolutionChartPanel();
    
    public EvolutionResultsPanel()
    {
        super(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Average Pay-Offs", averages);
        tabs.add("Population Composition", composition);
        tabs.add("Fixed Player Advantage", advantage);
        tabs.add("Fixed Player Rank", rank);
        tabs.add("Co-Evolution", coevo);
        add(tabs, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Evolution Results"));
    }
    
    
    public void setResults(EvolutionResult[] results)
    {
        averages.setData(results);
        composition.setData(results);
        advantage.setData(results);
        rank.setData(results);
        coevo.setData(results);
    }
    
    
    
    /**
     * Main method for running as a standalone data viewer.
     */
    public static void main(String[] args)
    {
        EvolutionResult[] results = EvolutionResult.loadResults(new java.io.File(args[0]));
        EvolutionResultsPanel panel = new EvolutionResultsPanel();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setResults(results);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}