// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class TitForTat extends AbstractPlayer
{
    public String getName()
    {
        return "TitForTat";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() == 0)
        {
            return Action.COOPERATE;
        }
        else
        {
            return history.getOpponentActionForIteration(this, history.getHistoryLength() - 1);
        }
    }
}