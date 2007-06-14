// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.RandomNumberGenerator;

/**
 * Implements a strategy that can't be beaten by a modeller that attempts to maximise its own
 * pay-off.
 */
public class ModellerNemesis extends AbstractPlayer
{
    private static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    private static final double DEFECT_PROBABILITY = 0.1;
    
    public String getName()
    {
        return "ModellerNemesis";
    }

    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() == 0)
        {
            return Action.COOPERATE;
        }
        else
        {
            Action action = history.getOpponentActionForIteration(this, history.getHistoryLength() - 1);
            if (action == Action.COOPERATE && RANDOM.nextDouble() < DEFECT_PROBABILITY)
            {
                action = Action.DEFECT;
            }
            return action;            
        }
    }
    
}