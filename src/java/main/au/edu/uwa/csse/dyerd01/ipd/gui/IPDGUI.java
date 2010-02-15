// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class provides initialisation and container code for running the GUI as
 * either a standalone application or as an applet.
 */
public class IPDGUI
{
    private final MainPanel mainPanel = new MainPanel();
    private final JFileChooser fileChooser = new JFileChooser();
    
    public IPDGUI()
    {
        JFrame frame = new JFrame("Iterated Prisoner's Dilemma");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setJMenuBar(createMenuBar());
        // frame.pack();
        frame.setSize(800, 600);
        frame.validate();
        frame.setVisible(true);
    }
    
    
    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        JMenuItem loadResultsMenuItem = new JMenuItem("Load Evolution Results...");
        loadResultsMenuItem.setMnemonic(KeyEvent.VK_L);
        fileMenu.add(loadResultsMenuItem);
        fileMenu.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(exitMenuItem);
        
        loadResultsMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                loadEvolutionResults();
            }
        });
        
        exitMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                System.exit(0);
            }
        });
        return menuBar;
    }
    
    
    private void loadEvolutionResults()
    {
        int result = fileChooser.showOpenDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            EvolutionResult[] results = EvolutionResult.loadResults(fileChooser.getSelectedFile());
            mainPanel.displayResults(results);
        }
    }

    
    public static void main(String[] args)
    {
        readConfig();
        PropertyConfigurator.configure(System.getProperties());
        new IPDGUI();
    }
    
    
    private static void readConfig()
    {
        try
        {
            System.getProperties().load(new FileInputStream("log4j.properties"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.err.println("Could not load log4j properties.");
            System.exit(-1);
        }
    }
}