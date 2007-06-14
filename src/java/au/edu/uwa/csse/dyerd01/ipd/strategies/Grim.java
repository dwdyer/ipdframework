// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * The GRIM (a.k.a TRIGGER or SPITEFUL) strategy for IPD.  Cooperates until its opponent
 * defects once and then plays ALWAYS DEFECT regardless of its opponent's actions.
 * @author Daniel Dyer
 */
public class Grim extends AbstractPlayer
{
    private Action nextAction = Action.COOPERATE;
    
    public String getName()
    {
        return "Grim";
    }
    
    
    public void reset()
    {
        nextAction = Action.COOPERATE;
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        if (nextAction == Action.COOPERATE
            && history.getHistoryLength() > 0
            && history.getOpponentActionForIteration(this, history.getHistoryLength() - 1) == Action.DEFECT)
        {
            nextAction = Action.DEFECT;
        }
        return nextAction;
    }
    
    
    public int getAggregatePayOffForOptimalCounterStrategy(int iterations)
    {
        return PayOff.MUTUAL_COOPERATION.getValue() * iterations;
    }
}