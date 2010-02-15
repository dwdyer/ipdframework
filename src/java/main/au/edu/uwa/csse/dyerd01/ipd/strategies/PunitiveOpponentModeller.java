// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractOpponentModeller;
import au.edu.uwa.csse.dyerd01.ipd.framework.OpponentModel;

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
    
    
    @Override
    protected OpponentModel createNewOpponentModel(int historyLength)
    {
        return new PunitiveOpponentModel(this);
    }
}