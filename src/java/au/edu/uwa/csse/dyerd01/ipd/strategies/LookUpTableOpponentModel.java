// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.OpponentModel;
import au.edu.uwa.csse.dyerd01.ipd.framework.PayOff;
import au.edu.uwa.csse.dyerd01.ipd.framework.Player;
import au.edu.uwa.csse.dyerd01.ipd.framework.RandomNumberGenerator;
import au.edu.uwa.csse.dyerd01.ipd.framework.TournamentManager;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class LookUpTableOpponentModel implements OpponentModel
{
    private static final Logger logger = Logger.getLogger(LookUpTableOpponentModel.class);
    protected static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    protected static final int LOOK_AHEAD = 5;
    
    private static final Action[][][][][] LOOK_UP_TABLE = new Action[10][10][10][10][];
    static
    {
        logger.debug("Pre-computing best responses...");
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                for (int k = 0; k < 10; k++)
                {
                    for (int l = 0; l < 10; l++)
                    {
                        LOOK_UP_TABLE[i][j][k][l] = getBestResponse(((double) i) / 10 + 0.05,
                                                                    ((double) j) / 10 + 0.05,
                                                                    ((double) k) / 10 + 0.05,
                                                                    ((double) l) / 10 + 0.05);
                    }
                }
            }
            logger.debug((i + 1) * 10 + "% done.");
        }
    }
    
    private static Action[] getBestResponse(double cc, double cd, double dc, double dd)
    {
        Action[] bestResponse = new Action[4];
        double cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.COOPERATE, Action.COOPERATE, LOOK_AHEAD);
        double defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.COOPERATE, Action.DEFECT, LOOK_AHEAD);
        bestResponse[0] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.DEFECT, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.DEFECT, Action.DEFECT, LOOK_AHEAD);
        bestResponse[1] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.COOPERATE, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.COOPERATE, Action.DEFECT, LOOK_AHEAD);        
        bestResponse[2] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.DEFECT, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.DEFECT, Action.DEFECT, LOOK_AHEAD);
        bestResponse[3] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;        

        return bestResponse;
    }
    
    private static double calculatePayOff(double cc,
                                          double cd,
                                          double dc,
                                          double dd,
                                          Action playerLastMove,
                                          Action opponentLastMove,
                                          Action chosenMove,
                                          int lookAhead)
    {
        if (lookAhead == 0)
        {
            return 0;
        }
        
        double cooperateProbability = 0;
        if (playerLastMove == Action.COOPERATE)
        {
            cooperateProbability = opponentLastMove == Action.COOPERATE ? cc : cd;
        }
        else
        {
            cooperateProbability = opponentLastMove == Action.COOPERATE ? dc : dd;
        }

        
        if (chosenMove == null)
        {
            double cooperatePayOff = cooperateProbability * (PayOff.MUTUAL_COOPERATION.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1))
                   + (1 - cooperateProbability) * (PayOff.EXPLOITATION_LOSER.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
            double defectPayOff = cooperateProbability * (PayOff.EXPLOITATION_WINNER.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1))
                   + (1 - cooperateProbability) * (PayOff.MUTUAL_DEFECTION.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
            return Math.max(cooperatePayOff, defectPayOff);
        }
        if (chosenMove == Action.COOPERATE)
        {
            return cooperateProbability * (PayOff.MUTUAL_COOPERATION.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1))
                   + (1 - cooperateProbability) * (PayOff.EXPLOITATION_LOSER.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
        }
        else
        {
            return cooperateProbability * (PayOff.EXPLOITATION_WINNER.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1))
                   + (1 - cooperateProbability) * (PayOff.MUTUAL_DEFECTION.getValue() + calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
        }
    }
    
    // Reference to the player that we are modelling on behalf of (NOT the player that is being modelled).
    private final Player player;
    private final ModelEntry[][] model;
    
    /**
     * Creates a new opponent model with the specified look-back size for the specified player
     * (NOTE: it does not model the specified player, but models that player's opponent.  The player
     * reference here is for extracting information from the game history).
     */
    public LookUpTableOpponentModel(Player player)
    {
        this.player = player;
        int size = exp(2, TournamentManager.HISTORY_LENGTH + 1) - 1;
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
        // If there is no relevant model entry just pick a move at random.
        if (history.getHistoryLength() == 0)
        {
            return RANDOM.nextDouble() < 0.5 ? Action.COOPERATE : Action.DEFECT;
        }
        
        int cc = getLookUpIndex(model[1][1]);
        int cd = getLookUpIndex(model[1][2]);
        int dc = getLookUpIndex(model[2][1]);
        int dd = getLookUpIndex(model[2][2]);
        Action[] counterStrategy = LOOK_UP_TABLE[cc][cd][dc][dd];
        int iteration = history.getHistoryLength() - 1;
        if (history.getPlayerActionForIteration(player, iteration) == Action.COOPERATE)
        {
            return history.getOpponentActionForIteration(player, iteration) == Action.COOPERATE ? counterStrategy[0] : counterStrategy[1];
        }
        else
        {
            return history.getOpponentActionForIteration(player, iteration) == Action.COOPERATE ? counterStrategy[2] : counterStrategy[3];
        }
    }
    
    
    private int getLookUpIndex(ModelEntry entry)
    {
        if (entry == null)
        {
            return RANDOM.nextInt(10);
        }
        return (int) Math.max((entry.getCooperationProbability() - 0.05) * 10, 0);
    }

    
    /**
     * Extracts the relevant sequence of moves from the history for updating or consulting
     * the model (look-back is limited by the history size of the model).
     */
    private Action[][] getRelevantHistory(GameHistory history, boolean update)
    {
        Action[][] relevantHistory = new Action[2][TournamentManager.HISTORY_LENGTH];
        int offset = history.getHistoryLength() - 1;
        for (int i = 0; i < Math.min(TournamentManager.HISTORY_LENGTH, offset); i++)
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
        assert history.length == TournamentManager.HISTORY_LENGTH : "Wrong history length.";
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
    protected static class ModelEntry
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