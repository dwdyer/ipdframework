// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;

/**
 * @author Daniel Dyer
 */
public abstract class AbstractPeriodicPlayer extends AbstractPlayer
{
    private final Action[] sequence;
    private int index = -1;
        
    protected AbstractPeriodicPlayer(Action[] sequence)
    {
        this.sequence = sequence;
    }
    
    public void reset()
    {
        index = -1;
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        index = ++index % sequence.length;
        return sequence[index];
    }
}