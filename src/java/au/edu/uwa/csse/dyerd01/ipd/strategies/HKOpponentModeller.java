// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class HKOpponentModeller extends AbstractPlayer
{
    private static Logger logger = Logger.getLogger(HKOpponentModeller.class);
    
    protected static final double FORGETTING_FACTOR = 0.95;
    protected static final int INCREMENT_VALUE = 1;
    protected static final double CONTINUATION_PROBABILITY = 0.99;
    
    private OpponentModel model;
    
    public String getName()
    {
        return "HKOpponentModeller";
    }
    
    
    public void reset()
    {
        model = createDefaultModel();
    }
    
    
    protected OpponentModel createDefaultModel()
    {
        double[][] probabilities = new double[2][2];
        for (int i = 0; i < probabilities.length; i++)
        {
            for (int j = 0; j < probabilities[i].length; j++)
            {
                double probability = RandomNumberGenerator.getInstance().nextDouble() * INCREMENT_VALUE;
                probabilities[i][j] = probability;
            }
        }
        return new OpponentModel(probabilities);
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        int historyLength = history.getHistoryLength();
        if (historyLength <= 1)
        {
            return model.getFirstMove();
        }
        else
        {
            // Update opponent model.
            Action[] playerHistory = new Action[]{history.getPlayerActionForIteration(this, historyLength - 2),
                                                  history.getOpponentActionForIteration(this, historyLength - 2)};
            model.update(playerHistory, history.getOpponentActionForIteration(this, historyLength - 1));
            playerHistory[0] = history.getPlayerActionForIteration(this, historyLength - 1);
            playerHistory[1] = history.getOpponentActionForIteration(this, historyLength - 1);
            return model.getNextMove(playerHistory);
        }
    }
    
    
    protected static class OpponentModel
    {
        private double[][] probabilities;
        
        public OpponentModel(double[][] probabilities)
        {
            this.probabilities = probabilities;
        }
        
        
        public void update(Action[] playerHistory, Action opponentAction)
        {
            double cooperationProbability = getModelElement(playerHistory);
            
            cooperationProbability *= FORGETTING_FACTOR;
            if (opponentAction == Action.COOPERATE)
            {
                cooperationProbability += INCREMENT_VALUE * (1 - FORGETTING_FACTOR);
            }
            else
            {
                cooperationProbability -= INCREMENT_VALUE * (1 - FORGETTING_FACTOR);
            }
            if (cooperationProbability < 0)
            {
                logger.error("ERROR: Negative probability.");
            }
        }
        
        
        public Action getNextMove(Action[] playerHistory)
        {
            double cooperationProbability = getModelElement(playerHistory);
            double defectionProbability = 1 - cooperationProbability;
            double cooperationValue = PayOff.MUTUAL_COOPERATION.getValue() * cooperationProbability + PayOff.EXPLOITATION_LOSER.getValue() * defectionProbability;
            double exploitationValue = PayOff.EXPLOITATION_WINNER.getValue() * cooperationProbability + PayOff.MUTUAL_DEFECTION.getValue() * defectionProbability;
            return cooperationValue > exploitationValue ? Action.COOPERATE : Action.DEFECT;
        }
        
        
        public Action getFirstMove()
        {
            return Action.COOPERATE;
        }

        
        private double getModelElement(Action[] history)
        {
            int x = history[0] == Action.COOPERATE ? 0 : 1;
            int y = history[1] == Action.COOPERATE ? 0 : 1;
            return probabilities[x][y];
        }
    }
}