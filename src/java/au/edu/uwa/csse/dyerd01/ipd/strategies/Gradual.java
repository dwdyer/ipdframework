// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import org.apache.log4j.Logger;

/**
 * An implementation of the GRADUAL strategy for playing the Iterated Prisoner's Dilemma,
 * as described by Beaufils, Delahaye and Mathieu.
 * @author Daniel Dyer
 */
public class Gradual extends AbstractPlayer
{
    private static Logger logger = Logger.getLogger(Gradual.class);
    
    protected static final int MODE_NORMAL = 0;
    protected static final int MODE_PUNISHMENT = 1;
    protected static final int MODE_RECONCILLIATION = 2;
    
    protected int defectionCount = 0;
    protected int mode = MODE_NORMAL;
    protected int modeDuration = 0;
    
    
    public String getName()
    {
        return "Gradual";
    }
    
    
    public void reset()
    {
        defectionCount = 0;
        mode = MODE_NORMAL;
        modeDuration = 0;
    }

    
    public Action getNextMove(GameHistory history)
    {
        int historyLength = history.getHistoryLength();
        if (mode == MODE_NORMAL
            && historyLength > 0
            && history.getOpponentActionForIteration(this, historyLength - 1) == Action.DEFECT)
        {
            mode = MODE_PUNISHMENT;
            defectionCount++;
            modeDuration = defectionCount;
            logger.debug("Gradual punishment triggered (duration: " + modeDuration + ").");
        }
        
        if (mode == MODE_PUNISHMENT)
        {
            logger.debug("In punishment mode.");
            if (modeDuration > 0)
            {
                logger.debug("Defecting...");
                modeDuration--;
                return Action.DEFECT;
            }
            else
            {
                logger.debug("Switched to reconcilliation.");
                mode = MODE_RECONCILLIATION;
                modeDuration = 2;
            }
        }
        if (mode == MODE_RECONCILLIATION && modeDuration <= 1)
        {
            mode = MODE_NORMAL;
        }
        modeDuration--;
        return Action.COOPERATE;
    }
}