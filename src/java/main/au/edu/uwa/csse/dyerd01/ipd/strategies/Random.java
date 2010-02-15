// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.RandomNumberGenerator;

/**
 * @author Daniel Dyer
 */
public class Random extends AbstractPlayer
{
    private static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    
    public String getName()
    {
        return "Random";
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        return RANDOM.nextDouble() >= 0.5 ? Action.COOPERATE : Action.DEFECT;
    }
}