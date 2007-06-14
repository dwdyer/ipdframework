// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework.evolution;

import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import java.util.EventListener;

/**
 * Listener interface to be implemented by classes that wish to register
 * for notifications about the evolution progress.
 * @author Daniel Dyer
 */
public interface EvolutionListener extends EventListener
{
    void notifyGenerationProcessed();
}