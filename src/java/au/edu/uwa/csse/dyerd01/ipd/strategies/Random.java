// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

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