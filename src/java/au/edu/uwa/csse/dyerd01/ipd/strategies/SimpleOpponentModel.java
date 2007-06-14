// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class SimpleOpponentModel implements OpponentModel
{
    private static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    
    // Reference to the player that we are modelling on behalf of (NOT the player that is being modelled).
    private final Player player;
    private final int historyLength;
    private final ModelEntry[][] model;
    
    /**
     * Creates a new opponent model with the specified look-back size for the specified player
     * (NOTE: it does not model the specified player, but models that player's opponent.  The player
     * reference here is for extracting information from the game history).
     */
    public SimpleOpponentModel(Player player, int historyLength)
    {
        this.player = player;
        this.historyLength = historyLength;
        int size = exp(2, historyLength + 1) - 1;
        model = new ModelEntry[size][size];
    }
    
    
    /**
     * Updates the model by extracting the most recent information from the game history.
     */
    public void updateModel(GameHistory history)
    {
        if (history.getHistoryLength() > 0)
        {
            updateModel(getRelevantHistory(history, true),
                        history.getOpponentActionForIteration(player, history.getHistoryLength() - 1));
        }
    }
    
    
    private void updateModel(Action[][] relevantHistory, Action opponentMove)
    {
        int modelIndexX = convertActionSequenceToModelIndex(relevantHistory[0]);
        int modelIndexY = convertActionSequenceToModelIndex(relevantHistory[1]);
        ModelEntry entry = model[modelIndexX][modelIndexY];
        if (entry == null)
        {
            entry = new ModelEntry();
            model[modelIndexX][modelIndexY] = entry;
        }
        entry.update(opponentMove);
    }
    
    
    public Action getBestResponse(GameHistory history)
    {
        Action[][] relevantHistory = getRelevantHistory(history, false);
        // If there is no relevant model entry just pick a move at random.
        if (model[convertActionSequenceToModelIndex(relevantHistory[0])][convertActionSequenceToModelIndex(relevantHistory[1])] == null)
        {
            return RANDOM.nextDouble() < 0.5 ? Action.COOPERATE : Action.DEFECT;
        }
        double cooperatePayOff = calculateFuturePayOff(relevantHistory, Action.COOPERATE, 3);
        double defectionPayOff = calculateFuturePayOff(relevantHistory, Action.DEFECT, 3);
        return cooperatePayOff >= defectionPayOff ? Action.COOPERATE : Action.DEFECT;
    }
    
    
    private double calculatePayOff(Action[][] relevantHistory, Action chosenMove)
    {
        double payOff = 0;
        int modelIndexX = convertActionSequenceToModelIndex(relevantHistory[0]);
        int modelIndexY = convertActionSequenceToModelIndex(relevantHistory[1]);
        ModelEntry entry = model[modelIndexX][modelIndexY];
        if (entry != null)
        {
            if (chosenMove == Action.COOPERATE)
            {
                double cooperatePayOff = entry.getCooperationProbability() * PayOff.MUTUAL_COOPERATION.getValue();
                double defectPayOff = (1 - entry.getCooperationProbability()) * PayOff.EXPLOITATION_LOSER.getValue();
                payOff = cooperatePayOff + defectPayOff;
            }
            else
            {
                double cooperatePayOff = entry.getCooperationProbability() * PayOff.EXPLOITATION_WINNER.getValue();
                double defectPayOff = (1 - entry.getCooperationProbability()) * PayOff.MUTUAL_DEFECTION.getValue();
                payOff = cooperatePayOff + defectPayOff;
            }
        }
        else
        {
            if (chosenMove == Action.COOPERATE)
            {
                payOff = ((double) PayOff.EXPLOITATION_LOSER.getValue() + PayOff.MUTUAL_COOPERATION.getValue()) / 2;
            }
            else
            {
                payOff = ((double) PayOff.EXPLOITATION_WINNER.getValue() + PayOff.MUTUAL_DEFECTION.getValue()) / 2;
            }
        }
        return payOff;
    }
    

    private double calculateFuturePayOff(Action[][] relevantHistory, Action chosenMove, int lookAhead)
    {
        if (lookAhead == 0)
        {
            return 0;
        }

        int modelIndexX = convertActionSequenceToModelIndex(relevantHistory[0]);
        int modelIndexY = convertActionSequenceToModelIndex(relevantHistory[1]);
        ModelEntry entry = model[modelIndexX][modelIndexY];
        double cooperationProbability;
        if (entry != null)
        {
            cooperationProbability = entry.getCooperationProbability();
        }
        else
        {
            // If there's no entry use a random number to aid exploration.
            cooperationProbability = RANDOM.nextDouble();
        }

        if (chosenMove == null)
        {
            double cooperatePayOff = calculatePayOff(relevantHistory, Action.COOPERATE);
            double defectionPayOff = calculatePayOff(relevantHistory, Action.DEFECT);
            Action[][] updatedHistory = new Action[2][historyLength];
            System.arraycopy(relevantHistory[0], 0, updatedHistory[0], 1, relevantHistory[0].length - 1);
            System.arraycopy(relevantHistory[1], 0, updatedHistory[1], 1, relevantHistory[1].length - 1);
                        
            double temp = 0;
            updatedHistory[0][0] = Action.COOPERATE;
            updatedHistory[1][0] = Action.COOPERATE;
            temp += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * cooperationProbability;
            updatedHistory[1][0] = Action.DEFECT;
            temp += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * (1 - cooperationProbability);
            cooperatePayOff += temp;
            
            temp = 0;
            updatedHistory[0][0] = Action.DEFECT;
            updatedHistory[1][0] = Action.COOPERATE;
            temp += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * cooperationProbability;
            updatedHistory[1][0] = Action.DEFECT;
            temp += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * (1 - cooperationProbability);
            defectionPayOff += temp;
            return Math.max(cooperatePayOff, defectionPayOff);
        }
        else
        {
            double payOff = calculatePayOff(relevantHistory, chosenMove);
            Action[][] updatedHistory = new Action[2][historyLength];
            System.arraycopy(relevantHistory[0], 0, updatedHistory[0], 1, relevantHistory[0].length - 1);
            System.arraycopy(relevantHistory[1], 0, updatedHistory[1], 1, relevantHistory[1].length - 1);
            updatedHistory[0][0] = chosenMove;
            updatedHistory[1][0] = Action.COOPERATE;
            payOff += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * cooperationProbability;
            updatedHistory[1][0] = Action.DEFECT;
            payOff += calculateFuturePayOff(updatedHistory, null, lookAhead - 1) * (1 - cooperationProbability);
            return payOff;
        }
    }
    

    /**
     * Extracts the relevant sequence of moves from the history for updating or consulting
     * the model (look-back is limited by the history size of the model).
     */
    private Action[][] getRelevantHistory(GameHistory history, boolean update)
    {
        Action[][] relevantHistory = new Action[2][historyLength];
        int offset = history.getHistoryLength() - 1;
        for (int i = 0; i < Math.min(historyLength, offset); i++)
        {
            if (update)
            {
                relevantHistory[0][i] = history.getPlayerActionForIteration(player, offset - 1 - i);
                relevantHistory[1][i] = history.getOpponentActionForIteration(player, offset - 1 - i);
            }
            else
            {
                relevantHistory[0][i] = history.getPlayerActionForIteration(player, offset - i);
                relevantHistory[1][i] = history.getOpponentActionForIteration(player, offset - i);
            }
        }
        return relevantHistory;
    }
    

    /**
     * Some maths to convert a particular sequence of actions into an index in a zero-based, contiguous,
     * one-dimensional array.
     */
    private int convertActionSequenceToModelIndex(Action[] history)
    {
        assert history.length == historyLength : "Wrong history length.";
        int index = 0;
        for (int i = 0; i < history.length; i++)
        {
            int value = (history[i] == Action.COOPERATE ? 1 : history[i] == Action.DEFECT ? 2 : 0);
            index += value * exp(2, i);
        }
        return index;
    }
    

    /**
     * Recursive helper method for calculating integer exponentials.
     */
    private int exp(int base, int exponent)
    {
        return exponent == 0 ? 1 : base * exp(base, exponent - 1);
    }
    
    
    /**
     * Inner class to encapsulate model data for a single game position.
     */
    private static class ModelEntry
    {
        private int cooperateCount = 0;
        private int iterationCount = 0;
        
        public double getCooperationProbability()
        {
            return ((double) cooperateCount) / iterationCount;
        }
        
        public void update(Action action)
        {
            if (action == Action.COOPERATE)
            {
                cooperateCount++;
            }
            iterationCount++;
        }
    }
}