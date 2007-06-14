// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines the operations that must be supported by an opponent model.
 * @author Daniel Dyer
 */
public interface OpponentModel
{
    void updateModel(GameHistory history);
    
    Action getBestResponse(GameHistory history);
}