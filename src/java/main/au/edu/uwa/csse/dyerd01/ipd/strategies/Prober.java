// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;

/**
 * An implementation of PROBER, one of the strategies used by Beaufils, Delahaye and Mathieu
 * to evaluate their GRADUAL strategy.  Probes for naive strategies like ALWAYS COOPERATE and
 * exploits them.  Plays TIT-FOR-TAT against strategies that it doesn't recognise as exploitable.
 * @author Daniel Dyer
 */
public class Prober extends TitForTat
{
    private boolean playAlwaysDefect = false;
    
    @Override
    public String getName()
    {
        return "Prober";
    }
    
    
    @Override
    public Action getNextMove(GameHistory history)
    {
        switch (history.getHistoryLength())
        {
            case 0: return Action.DEFECT;
            case 1: return Action.COOPERATE;
            case 2: return Action.COOPERATE;
            case 3: playAlwaysDefect = (history.getOpponentActionForIteration(this, 1) == Action.COOPERATE
                                        && history.getOpponentActionForIteration(this, 2) == Action.COOPERATE);
            default: return playAlwaysDefect ? Action.DEFECT : super.getNextMove(history);
        }
    }
}