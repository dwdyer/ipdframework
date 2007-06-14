// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class Majority extends AbstractPlayer
{
    // Cooperate while this value is non-negative, defect otherwise.
    private int counter = 0;
    
    public String getName()
    {
        return "Majority";
    }
    
    
    public void reset()
    {
        counter = 0;
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() > 0)
        {
            if (history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT)
            {
                counter--;
            }
            else
            {
                counter++;
            }
        }
        return counter >= 0 ? Action.COOPERATE : Action.DEFECT;
    }
    
    
    public double getAveragePayOffForOptimalCounterStrategy()
    {
        return 4;
    }
}