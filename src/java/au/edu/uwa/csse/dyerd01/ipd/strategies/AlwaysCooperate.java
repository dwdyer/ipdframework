// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * A naive IPD strategy that cooperates on every move regardless of the game history.
 * @author Daniel Dyer
 */
public class AlwaysCooperate extends AbstractPlayer
{
    public String getName()
    {
        return "AlwaysCooperate";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        return Action.COOPERATE;
    }
    
    
    public int getAggregatePayOffForOptimalCounterStrategy(int iterations)
    {
        return PayOff.EXPLOITATION_WINNER.getValue() * iterations;
    }
}