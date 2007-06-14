// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.Scrollable;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.CategoryTableXYDataset;

public class PopulationCompositionChartPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(PopulationCompositionChartPanel.class);
    
    private static final String[] STRATEGY_NAMES = new String[]{"0(AD)", "1", "2", "3", "4(AD)", "5", "6", "7",
                                                                "8(AD)", "9(SPav)", "10(STFT)", "11", "12(AD)", "13", "14", "15",
                                                                "16", "17", "18", "19", "20", "21", "22", "23",
                                                                "24(Grim)", "25(Pav)", "26(TFT)", "27", "28(AC)", "29(AC)", "30(AC)", "31(AC)"};
    
    
    private final JCheckBox[] filters = new JCheckBox[STRATEGY_NAMES.length];
    private final JCheckBox othersCheckBox = new JCheckBox("Others", false);
    private final JCheckBox acCheckBox = new JCheckBox("AC(+V)", false);
    private final JCheckBox adCheckBox = new JCheckBox("AD(+V)", false);
    private final JCheckBox tftCheckBox = new JCheckBox("TFT/STFT", false);
    private final JCheckBox pavCheckBox = new JCheckBox("Pav/SPav", false);
    private EvolutionResult[] cachedData;
    
    public PopulationCompositionChartPanel()
    {
        super(new BorderLayout());
        add(createFilterPanel(), BorderLayout.EAST);
    }
    
    private JComponent createFilterPanel()
    {
        ScrollablePanel filterPanel = new ScrollablePanel(new GridLayout(38, 1));
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
        for (int i = 0; i < STRATEGY_NAMES.length; i++)
        {
            boolean selected = false;
            switch (i)
            {
                case 0:
                case 24:
                case 25:
                case 26:
                case 31:
                    selected = true;
            }
            filters[i] = new JCheckBox(STRATEGY_NAMES[i], selected);
            filters[i].addItemListener(checkBoxListener);
            filterPanel.add(filters[i]);
        }
        othersCheckBox.addItemListener(checkBoxListener);
        adCheckBox.addItemListener(checkBoxListener);
        pavCheckBox.addItemListener(checkBoxListener);
        tftCheckBox.addItemListener(checkBoxListener);
        acCheckBox.addItemListener(checkBoxListener);
        filterPanel.add(othersCheckBox);
        filterPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        filterPanel.add(adCheckBox);
        filterPanel.add(pavCheckBox);
        filterPanel.add(tftCheckBox);        
        filterPanel.add(acCheckBox);
        JScrollPane scroller = new JScrollPane(filterPanel);
        scroller.setBorder(BorderFactory.createTitledBorder("Filter"));
        return scroller;
    }

    
    public void setData(final EvolutionResult[] data)
    {
        logger.debug("Displaying data for " + data.length + " records.");
        cachedData = data;
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (int i = 0; i < data.length; i++)
        {
            int[] classifications = data[i].getClassifications();
            int others = 0;
            int acCount = 0;
            int adCount = 0;
            int tftCount = 0;
            int pavCount = 0;
            for (int j = 0; j < classifications.length; j++)
            {
                if (filters[j].isSelected())
                {
                    dataSet.add(i, classifications[j], STRATEGY_NAMES[j]);
                }
                else
                {
                    others += classifications[j];
                }
                
                switch (j)
                {
                    case 0:
                    case 4:
                    case 8:
                    case 12:
                        adCount += classifications[j];
                        break;
                    case 9:
                    case 25:
                        pavCount += classifications[j];
                        break;
                    case 10:
                    case 26:
                        tftCount += classifications[j];
                        break;
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                        acCount += classifications[j];
                        break;
                }
            }            
            if (othersCheckBox.isSelected())
            {
                dataSet.add(i, others, "Others");
            }
            if (adCheckBox.isSelected())
            {
                dataSet.add(i, adCount, "AD(+V)");
            }
            if (pavCheckBox.isSelected())
            {
                dataSet.add(i, pavCount, "Pav/SPav");
            }
            if (tftCheckBox.isSelected())
            {
                dataSet.add(i, tftCount, "TFT/STFT");
            }            
            if (acCheckBox.isSelected())
            {
                dataSet.add(i, acCount, "AC(+V)");
            }
        }
        JFreeChart chart = ChartFactory.createLineXYChart(null, // "Population Composition", // Chart title
                                                          "No. Generations", // X-axis label
                                                          "Count", // Y-axis label
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
    
    
    private static final class ScrollablePanel extends JPanel implements Scrollable
    {
        public ScrollablePanel(LayoutManager layout)
        {
            super(layout);
        }
        
        public boolean getScrollableTracksViewportHeight()
        {
            return false;
        }
        
        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }
                
        public Dimension getPreferredScrollableViewportSize()
        {
            Dimension size = getMinimumSize();
            size.width = (int) (1.5 * size.width);
            return size;
        }
        
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
        {
            return 12;
        }
        
        
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
        {
            return 12;
        }
    }
}