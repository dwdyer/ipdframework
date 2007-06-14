// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * Opponent modeller that caches pre-computed best responses for better performance.
 * @author Daniel Dyer
 */
public class ApproximatingOpponentModeller extends AbstractOpponentModeller
{
    public String getName()
    {
        return "ApproximatingOpponentModeller";
    }
    
    
    protected OpponentModel createNewOpponentModel(int historyLength)
    {
        return new LookUpTableOpponentModel(this);
    }
}