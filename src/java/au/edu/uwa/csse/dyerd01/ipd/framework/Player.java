// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines methods that must be implemented by an IPD strategy implementation.
 * @author Daniel Dyer
 */
public interface Player
{
    /**
     * @return The name of this strategy/player.
     */
    public String getName();

    
    /**
     * This method is called before commencing a series of rounds against a new opponent.
     * Sub-classes should over-ride this to reset any internal state.
     */
    public void reset();
    
    
    /**
     * Get the action to take in the next round.  Implementing classes
     * can use any logic they like to make the decision.
     */
    public Action getNextMove(GameHistory history);
}