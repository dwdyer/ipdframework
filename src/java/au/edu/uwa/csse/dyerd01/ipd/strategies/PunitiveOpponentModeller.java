// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * Tries to maximise winning margin over opponent.
 * Opponent modeller that caches pre-computed best responses for better performance.
 * @author Daniel Dyer
 */
public class PunitiveOpponentModeller extends AbstractOpponentModeller
{
    public String getName()
    {
        return "PunitiveOpponentModeller";
    }
    
    
    protected OpponentModel createNewOpponentModel(int historyLength)
    {
        return new PunitiveOpponentModel(this);
    }
}