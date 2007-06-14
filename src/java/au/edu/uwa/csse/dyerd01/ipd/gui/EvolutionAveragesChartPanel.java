// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.data.CategoryTableXYDataset;

public class EvolutionAveragesChartPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(EvolutionAveragesChartPanel.class);
        
    private final JCheckBox populationAverageCheckBox = new JCheckBox("Population Average", true);
    private final JCheckBox populationFitnessCheckBox = new JCheckBox("Population Fitness", false);
    private final JCheckBox populationBestCheckBox = new JCheckBox("Population Best", false);
    private final JCheckBox fixedPlayerAverageCheckBox = new JCheckBox("Fixed Player Average", true);
    
    private EvolutionResult[] cachedData;
    
    public EvolutionAveragesChartPanel()
    {
        super(new BorderLayout());
        add(createFilterPanel(), BorderLayout.NORTH);
    }
    
    
    private JComponent createFilterPanel()
    {
        JPanel filterPanel = new JPanel(new GridLayout(1, 4));
        ItemListener checkBoxListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                if (cachedData != null)
                {
                    setData(cachedData);
                }
            }
        };
        populationAverageCheckBox.addItemListener(checkBoxListener);
        populationFitnessCheckBox.addItemListener(checkBoxListener);
        populationBestCheckBox.addItemListener(checkBoxListener);
        fixedPlayerAverageCheckBox.addItemListener(checkBoxListener);
        filterPanel.add(populationAverageCheckBox);
        filterPanel.add(populationFitnessCheckBox);
        filterPanel.add(populationBestCheckBox);
        filterPanel.add(fixedPlayerAverageCheckBox);        
        filterPanel.setBorder(BorderFactory.createTitledBorder("Display Options"));
        return filterPanel;
    }
    
    
    public void setData(final EvolutionResult[] data)
    {
        cachedData = data;        
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (int i = 0; i < data.length; i++)
        {
            if (populationAverageCheckBox.isSelected())
            {
                dataSet.add(i, data[i].getAveragePayOff(), "Population Average");
            }
            if (populationBestCheckBox.isSelected())
            {
                dataSet.add(i, data[i].getBestPayOff(), "Population Best");
            }
            if (populationFitnessCheckBox.isSelected())
            {
                dataSet.add(i, data[i].getFitness(), "Population Fitness");
            }
            if (fixedPlayerAverageCheckBox.isSelected())
            {
                dataSet.add(i, data[i].getFixedPlayerAverage(), "Fixed Player Average");
            }
        }

        JFreeChart chart = ChartFactory.createLineXYChart(null, // "Population Average vs. Fixed Player Average", // Chart title
                                                          "No. Generations", // X-axis label
                                                          "Average Pay-Off", // Y-axis label
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