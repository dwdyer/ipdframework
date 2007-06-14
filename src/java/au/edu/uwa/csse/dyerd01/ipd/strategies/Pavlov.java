// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class Pavlov extends AbstractPlayer
{
    private Action nextAction = null;
    
    public String getName()
    {
        return "Pavlov";
    }
    
    
    public void reset()
    {
        // nextAction = Math.random() >= 0.5 ? Action.COOPERATE : Action.DEFECT;
        nextAction = Action.COOPERATE;
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() > 0
            && history.getPlayerPayOffForIteration(this, history.getHistoryLength() - 1).getValue() < PayOff.MUTUAL_COOPERATION.getValue())
        {
            nextAction = nextAction == Action.DEFECT ? Action.COOPERATE : Action.DEFECT;
        }
        return nextAction;
    }
}