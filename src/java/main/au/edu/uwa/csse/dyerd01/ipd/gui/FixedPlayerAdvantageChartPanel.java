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

public class FixedPlayerAdvantageChartPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(FixedPlayerAdvantageChartPanel.class);
    
    public FixedPlayerAdvantageChartPanel()
    {
        super(new BorderLayout());
    }
    
    public void setData(final EvolutionResult[] data)
    {
        logger.debug("Displaying data for " + data.length + " records.");
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (int i = 0; i < data.length; i++)
        {
            dataSet.add(i, data[i].getFixedPlayerAverage() - data[i].getAveragePayOff(), "Fixed Player Advantage");
        }
        JFreeChart chart = ChartFactory.createLineXYChart("Difference Between Fixed Player Average and Population Average", // Chart title
                                                          "No. Generations", // X-axis label
                                                          "Average Pay-Off Difference", // Y-axis label
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Include legend
                                                          true, // Tooltips
                                                          false); // URLs

        // Customise chart.
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setMinimumAxisValue(0);
//         NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//         rangeAxis.setMinimumAxisValue(-2);
//         rangeAxis.setMaximumAxisValue(2);
        
        removeAll();
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }
}