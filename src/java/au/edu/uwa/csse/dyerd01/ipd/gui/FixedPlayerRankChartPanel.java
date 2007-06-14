// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.CategoryTableXYDataset;

public class FixedPlayerRankChartPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(FixedPlayerRankChartPanel.class);
    
    public FixedPlayerRankChartPanel()
    {
        super(new BorderLayout());
    }
    
    public void setData(final EvolutionResult[] data)
    {
//         // Count how big the population was.
//         int populationSize = 0;
//         int[] classifications = data[0].getClassifications();
//         for (int i = 0; i < classifications.length; i++)
//         {
//             populationSize += classifications[i];
//         }
        
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (int i = 0; i < data.length; i++)
        {
            dataSet.add(i, data[i].getFixedPlayerRank(), "Fixed Player Rank");
        }
        JFreeChart chart = ChartFactory.createLineXYChart("Fixed Player Performance Ranked Against Population", // Chart title
                                                          "No. Generations", // X-axis label
                                                          "Rank", // Y-axis label
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Include legend
                                                          true, // Tooltips
                                                          false); // URLs

        // Customise chart.
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        // rangeAxis.setMinimumAxisValue(1);
        // rangeAxis.setMaximumAxisValue(populationSize + 1);
        rangeAxis.setInverted(true);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setMinimumAxisValue(0);
        
        removeAll();
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }
}