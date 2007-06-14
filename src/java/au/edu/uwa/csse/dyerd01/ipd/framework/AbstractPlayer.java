// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Convenience base class for implementing IPD strategies.
 * @author Daniel Dyer
 */
public abstract class AbstractPlayer implements Player
{
    /**
     * Default (no-op) implementation of reset method specified by Player interface.
     * Sub-classes should over-ride this to reset any internal state.
     */
    public void reset()
    {
        // Do nothing.
    }
    
    
    public String toString()
    {
        return getName();
    }
}