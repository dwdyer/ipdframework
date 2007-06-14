// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import au.edu.uwa.csse.dyerd01.ipd.framework.Player;
import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import au.edu.uwa.csse.dyerd01.ipd.framework.TournamentManager;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionListener;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

/**
 * @author Daniel Dyer
 */
public class ControlPanel extends JPanel
{
    private SpinnerNumberModel roundsSpinnerModel, noiseSpinnerModel;
    private JCheckBox playSelfCheckBox;
    
    /**
     * Uses the externals pattern for inversion of control to send results to
     * an interested object.
     */
    public static interface Externals
    {
        void displayResults(RoundRobinResult[] results);
        void displayResults(EvolutionResult[] results);
    }

    private final Externals externals;
    
    public ControlPanel(Externals externals)
    {
        super(new BorderLayout());
        this.externals = externals;
        add(createTournamentPanel(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Pre-Defined Strategies", createPlayerSelectionPanel());
        tabs.add("Evolution", createEvolutionPanel());
        tabs.setBorder(BorderFactory.createTitledBorder("Strategy Population"));
        add(tabs, BorderLayout.CENTER);
    }

    
    
    private JComponent createPlayerSelectionPanel()
    {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel playerSelectionPanel = new JPanel(new GridBagLayout());
        playerSelectionPanel.setBorder(BorderFactory.createTitledBorder("Strategy Selection"));
        GridBagConstraints constraints = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                               GridBagConstraints.RELATIVE,
                                                               GridBagConstraints.REMAINDER,
                                                               1, 1.0, 0.0,
                                                               GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets(0, 0, 0, 0),
                                                               0, 0);
        
        // Wrap strategy classes for display in combo box.
        Class[] strategies = TournamentManager.getInstance().getAvailableStrategies();
        StrategyClassWrapper[] wrappers = new StrategyClassWrapper[strategies.length];
        for (int i = 0; i < strategies.length; i++)
        {
            wrappers[i] = new StrategyClassWrapper(strategies[i]);
        }
        
        final JComboBox strategySelector = new JComboBox(wrappers);
        final DefaultListModel selectedStrategiesModel = new DefaultListModel();
        final JList selectedStrategies = new JList(selectedStrategiesModel);
        JButton addStrategyButton = new JButton("Add");
        // Add button listener to copy strategy from selector to list of selected strategies.
        addStrategyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                selectedStrategiesModel.addElement(strategySelector.getSelectedItem());
            }
        });
        JButton removeStrategyButton = new JButton("Remove");
        // Add button listener to remove strategies from list of selected strategies.
        removeStrategyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                Object[] strategiesToRemove = selectedStrategies.getSelectedValues();
                for (int i = 0; i < strategiesToRemove.length; i++)
                {
                    selectedStrategiesModel.removeElement(strategiesToRemove[i]);
                }
            }
        });
        
        // Set tooltip for initial combo box state.
        strategySelector.setToolTipText(((StrategyClassWrapper) strategySelector.getSelectedItem()).getStrategyClass().getName());
        // Add listener to update tooltip as selection changes.
        strategySelector.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                strategySelector.setToolTipText(((StrategyClassWrapper) strategySelector.getSelectedItem()).getStrategyClass().getName());
            }
        });
        
        playerSelectionPanel.add(strategySelector, constraints);
        
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        playerSelectionPanel.add(addStrategyButton, constraints);
        
        constraints.anchor = GridBagConstraints.WEST;
        playerSelectionPanel.add(new JLabel("Selected Strategies:"), constraints);
                                 
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        playerSelectionPanel.add(new JScrollPane(selectedStrategies), constraints);
        
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weighty = 0.0;
        playerSelectionPanel.add(removeStrategyButton, constraints);
        
        JButton startButton = new JButton("Start Tournament");
        startButton.setFont(new Font("Dialog", Font.BOLD, 12));
        outerPanel.add(playerSelectionPanel, BorderLayout.CENTER);
        outerPanel.add(startButton, BorderLayout.SOUTH);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                try
                {
                    Object[] wrappers = selectedStrategiesModel.toArray();
                    Player[] players = new Player[wrappers.length];
                    for (int i = 0; i < wrappers.length; i++)
                    {
                        players[i] = (Player) ((StrategyClassWrapper) wrappers[i]).getStrategyClass().newInstance();
                    }
                    int noOfRounds = roundsSpinnerModel.getNumber().intValue();
                    double noiseProbability = noiseSpinnerModel.getNumber().doubleValue();
                    RoundRobinResult[] results = TournamentManager.getInstance().executeRoundRobinTournament(players, noOfRounds, noiseProbability, playSelfCheckBox.isSelected());
                    externals.displayResults(results);
                }
                catch (Exception ex)
                {
                    // TO DO: Proper exception handling.
                    ex.printStackTrace();
                }
            }
        });
                                 
        return outerPanel;
    }
    
    
    private JComponent createEvolutionPanel()
    {
        JPanel outerPanel = new JPanel(new BorderLayout());
        GridBagConstraints constraints = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                GridBagConstraints.RELATIVE,
                                                                GridBagConstraints.RELATIVE,
                                                                1, 1.0, 1.0,
                                                                GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets(0, 0, 0, 0),
                                                                0, 0);

        // Strategy parameters.
        JPanel strategyPanel = new JPanel(new GridBagLayout());
        strategyPanel.setBorder(BorderFactory.createTitledBorder("Strategy Parameters"));
        final JRadioButton mixedStrategies = new JRadioButton("Mixed Strategies", true);
        final JRadioButton pureStrategies = new JRadioButton("Pure Strategies");
        ButtonGroup strategyTypeButtonGroup = new ButtonGroup();
        strategyTypeButtonGroup.add(mixedStrategies);
        strategyTypeButtonGroup.add(pureStrategies);
        strategyPanel.add(mixedStrategies, constraints);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        strategyPanel.add(pureStrategies, constraints);
        
        // Evolution parameters.
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        JPanel evolutionPanel = new JPanel(new GridBagLayout());
        evolutionPanel.setBorder(BorderFactory.createTitledBorder("Evolution Parameters"));
        
        evolutionPanel.add(new JLabel("Number of Generations:"), constraints);
        final SpinnerNumberModel generationsSpinnerModel = new SpinnerNumberModel(50, 1, 10000, 1);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        evolutionPanel.add(new JSpinner(generationsSpinnerModel), constraints);
        
        constraints.gridwidth = GridBagConstraints.RELATIVE;        
        evolutionPanel.add(new JLabel("Population Size:"), constraints);
        final SpinnerNumberModel populationSpinnerModel = new SpinnerNumberModel(100, 1, 500, 1);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        evolutionPanel.add(new JSpinner(populationSpinnerModel), constraints);

        constraints.gridwidth = GridBagConstraints.RELATIVE;        
        evolutionPanel.add(new JLabel("Mutation Probability:"), constraints);
        final SpinnerNumberModel mutationSpinnerModel = new SpinnerNumberModel(0.1, 0.005, 1.00, 0.005);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        evolutionPanel.add(new JSpinner(mutationSpinnerModel), constraints);
        
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        final JLabel standardDeviationLabel = new JLabel("Mutation Standard Deviation:");
        evolutionPanel.add(standardDeviationLabel, constraints);
        final SpinnerNumberModel standardDeviationSpinnerModel = new SpinnerNumberModel(0.1, 0.01, 3.00, 0.01);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        final JSpinner standardDeviationSpinner = new JSpinner(standardDeviationSpinnerModel);
        evolutionPanel.add(standardDeviationSpinner, constraints);
        // Disable the standard deviation parameter for pure strategies.
        pureStrategies.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                boolean enabled = !pureStrategies.isSelected();
                standardDeviationLabel.setEnabled(enabled);
                standardDeviationSpinner.setEnabled(enabled);
            }
        });
        
        // Opponent parameters.
        JPanel opponentPanel = new JPanel(new GridBagLayout());
        opponentPanel.setBorder(BorderFactory.createTitledBorder("Fixed Opponent"));
        final JCheckBox playFixedOpponent = new JCheckBox("Play also against fixed opponent?");
        opponentPanel.add(playFixedOpponent, constraints);
        
        // Wrap strategy classes for display in combo box.
        Class[] strategies = TournamentManager.getInstance().getAvailableStrategies();
        StrategyClassWrapper[] wrappers = new StrategyClassWrapper[strategies.length];
        for (int i = 0; i < strategies.length; i++)
        {
            wrappers[i] = new StrategyClassWrapper(strategies[i]);
        }
        final JComboBox opponentSelector = new JComboBox(wrappers);

        opponentPanel.add(opponentSelector, constraints);        
        final JCheckBox fitnessCheckBox = new JCheckBox("Include in fitness scores");
        opponentPanel.add(fitnessCheckBox, constraints);
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        final JLabel weightingLabel = new JLabel("Weighting (%):");
        opponentPanel.add(weightingLabel, constraints);
        final SpinnerNumberModel weightingSpinnerModel = new SpinnerNumberModel(50, 1, 100, 1);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        final JSpinner weightingSpinner = new JSpinner(weightingSpinnerModel);
        opponentPanel.add(weightingSpinner, constraints);
        playFixedOpponent.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                boolean enabled = playFixedOpponent.isSelected();
                opponentSelector.setEnabled(enabled);
                fitnessCheckBox.setEnabled(enabled);
                weightingLabel.setEnabled(enabled);
                weightingSpinner.setEnabled(enabled);
            }
        });
        opponentSelector.setEnabled(false);
        fitnessCheckBox.setEnabled(false);
        weightingLabel.setEnabled(false);
        weightingSpinner.setEnabled(false);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(strategyPanel, BorderLayout.NORTH);
        wrapperPanel.add(evolutionPanel, BorderLayout.CENTER);
        wrapperPanel.add(opponentPanel, BorderLayout.SOUTH);
        outerPanel.add(wrapperPanel, BorderLayout.NORTH);
        
        JButton startButton = new JButton("Start Evolution");
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                final int noOfGenerations = generationsSpinnerModel.getNumber().intValue();
                final ProgressDialog progressDialog = new ProgressDialog(null, noOfGenerations); // TO DO: Proper owner.
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            int noOfRounds = roundsSpinnerModel.getNumber().intValue();
                            double noiseProbability = noiseSpinnerModel.getNumber().doubleValue();
                            boolean playSelf = playSelfCheckBox.isSelected();
                            boolean pureStrategiesOnly = pureStrategies.isSelected();
                            int populationSize = populationSpinnerModel.getNumber().intValue();
                            double mutationProbability = mutationSpinnerModel.getNumber().doubleValue();
                            double standardDeviation = standardDeviationSpinnerModel.getNumber().doubleValue();
                            int weighting = weightingSpinnerModel.getNumber().intValue();
                        
                            Player fixedOpponent = playFixedOpponent.isSelected() ? (Player) ((StrategyClassWrapper) opponentSelector.getSelectedItem()).getStrategyClass().newInstance() : null;
                        
                            EvolutionListener evolutionListener = new EvolutionListener()
                            {
                                public void notifyGenerationProcessed()
                                {
                                    progressDialog.increment();
                                }
                            };
                            EvolutionResult[] results = TournamentManager.getInstance().executeEvolution(pureStrategiesOnly,
                                                                                                         noOfGenerations,
                                                                                                         populationSize,
                                                                                                         mutationProbability,
                                                                                                         standardDeviation,
                                                                                                         noOfRounds,
                                                                                                         noiseProbability,
                                                                                                         playSelf,
                                                                                                         fixedOpponent,
                                                                                                         weighting,
                                                                                                         evolutionListener);
                            externals.displayResults(results);
                        }
                        catch (Exception ex)
                        {
                            // TO DO: Proper exception handling.
                            ex.printStackTrace();
                        }
                    }
                }).start();
                progressDialog.show();
            }
        });
        
        outerPanel.add(startButton, BorderLayout.SOUTH);

        return outerPanel;
    }
    
    
    private JComponent createTournamentPanel()
    {
        JPanel tournamentPanel = new JPanel(new GridBagLayout());
        tournamentPanel.setBorder(BorderFactory.createTitledBorder("Tournament Parameters"));
        
        GridBagConstraints constraints = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                GridBagConstraints.RELATIVE,
                                                                GridBagConstraints.RELATIVE,
                                                                1, 1.0, 1.0,
                                                                GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets(0, 0, 0, 0),
                                                                0, 0);

        tournamentPanel.add(new JLabel("Rounds per head-to-head:"), constraints);
        roundsSpinnerModel = new SpinnerNumberModel(1000, 0, 10000, 1);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        tournamentPanel.add(new JSpinner(roundsSpinnerModel), constraints);
        
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        tournamentPanel.add(new JLabel("Probability of noise:"), constraints);
        noiseSpinnerModel = new SpinnerNumberModel(0.0, 0, 1.00, 0.01);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        tournamentPanel.add(new JSpinner(noiseSpinnerModel), constraints);
        
        playSelfCheckBox = new JCheckBox("Strategies play self?");
        tournamentPanel.add(playSelfCheckBox, constraints);
        
        return tournamentPanel;
    }
    
    
    /**
     * Wrapper for a class object to provide a tidier String representation for
     * use in combo boxes.
     */
    private static class StrategyClassWrapper
    {
        private final Class classObject;
        
        public StrategyClassWrapper(Class classObject)
        {
            this.classObject = classObject;
        }

        
        public Class getStrategyClass()
        {
            return classObject;
        }

        
        public String toString()
        {
            String fullyQualifiedName = classObject.getName();
            return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1);
        }
    }
}