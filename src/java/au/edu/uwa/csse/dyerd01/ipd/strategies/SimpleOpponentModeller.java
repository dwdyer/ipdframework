// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class SimpleOpponentModeller extends AbstractOpponentModeller
{
    public String getName()
    {
        return "SimpleOpponentModeller";
    }
    
    
    protected OpponentModel createNewOpponentModel(int historyLength)
    {
        return new SimpleOpponentModel(this, historyLength);
    }
}