// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;

/**
 * @author Daniel Dyer
 */
public class Strategy18 extends AbstractPlayer
{
    public String getName()
    {
        return "Strategy18";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() == 0
            || (history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.COOPERATE
                && history.getPlayerActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT))
        {
            return Action.COOPERATE;
        }
        return Action.DEFECT;
    }
}
