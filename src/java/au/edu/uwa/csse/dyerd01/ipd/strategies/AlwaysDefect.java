// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;

/**
 * @author Daniel Dyer
 */
public class AlwaysDefect extends AbstractPlayer
{
    public String getName()
    {
        return "AlwaysDefect";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        return Action.DEFECT;
    }
}