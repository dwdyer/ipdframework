// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.PayOff;

/**
 * @author Daniel Dyer
 */
public class TitFor2Tats extends AbstractPlayer
{
    public String getName()
    {
        return "TitFor2Tats";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() > 1
            && history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT
            && history.getOpponentActionForIteration(this, history.getHistoryLength() - 2) == Action.DEFECT)
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
        return (PayOff.EXPLOITATION_WINNER.getValue() + PayOff.MUTUAL_COOPERATION.getValue()) * iterations / 2;
    }
}