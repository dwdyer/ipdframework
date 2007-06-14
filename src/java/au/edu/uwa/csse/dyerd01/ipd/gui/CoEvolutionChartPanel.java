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

public class CoEvolutionChartPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(CoEvolutionChartPanel.class);
        
    public CoEvolutionChartPanel()
    {
        super(new BorderLayout());
    }
    
    
    public void setData(final EvolutionResult[] data)
    {
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (int i = 0; i < data.length; i++)
        {
            dataSet.add(i, data[i].getAverageRatio(), "Average Fitness Ratio");
            // dataSet.add(i, data[i].getMaxRatio(), "Maximum Fitness Ratio");
            // dataSet.add(i, data[i].getMinRatio(), "Minimum Fitness Ratio");
        }

        JFreeChart chart = ChartFactory.createLineXYChart(null, // Chart title
                                                          "No. Generations", // X-axis label
                                                          "Fitness Ratio", // Y-axis label
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Include legend
                                                          true, // Tooltips
                                                          false); // URLs
        
        // Customise chart.
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setMinimumAxisValue(0);

        if (getComponentCount() > 1)
        {
            remove(1);
        }

        add(new ChartPanel(chart), BorderLayout.CENTER);
        revalidate();
    }
}