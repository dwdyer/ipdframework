// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class SuspiciousTitForTat extends AbstractPlayer
{
    public String getName()
    {
        return "SuspiciousTitForTat";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() == 0
            || history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT)
        {
            return Action.DEFECT;
        }
        else
        {
            return Action.COOPERATE;
        }
    }
    
    
    public int getAggregatePayOffForOptimalCounterStrategy(int iterations)
    {
        return PayOff.EXPLOITATION_LOSER.getValue() + PayOff.MUTUAL_COOPERATION.getValue() * (iterations - 1);
    }
}