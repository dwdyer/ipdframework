// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class Strategy27 extends AbstractPlayer
{
    public String getName()
    {
        return "Strategy27";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() != 0
            && history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT
            && history.getPlayerActionForIteration(this, history.getHistoryLength() - 1) == Action.COOPERATE)
        {
            return Action.DEFECT;
        }
        return Action.COOPERATE;
    }
}
