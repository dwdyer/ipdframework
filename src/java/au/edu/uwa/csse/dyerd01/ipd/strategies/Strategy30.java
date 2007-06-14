// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * <pre>(C)CCCD</pre>
 * This strategy is effectively the same as Always Cooperate since the mutual defection state
 * is unreachable.
 * @author Daniel Dyer
 */
public class Strategy30 extends AbstractPlayer
{
    public String getName()
    {
        return "Strategy30";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() != 0
            && history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT
            && history.getPlayerActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT)
        {
            return Action.DEFECT;
        }
        return Action.COOPERATE;
    }
}
