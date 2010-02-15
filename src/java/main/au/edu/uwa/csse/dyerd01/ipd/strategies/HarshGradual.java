// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import org.apache.log4j.Logger;

/**
 * An implementation of the GRADUAL strategy for playing the Iterated Prisoner's Dilemma,
 * as described by Beaufils, Delahaye and Mathieu.
 * @author Daniel Dyer
 */
public class HarshGradual extends Gradual
{
    private static final Logger logger = Logger.getLogger(HarshGradual.class);
    
    @Override
    public String getName()
    {
        return "HarshGradual";
    }
    
    
    @Override
    public Action getNextMove(GameHistory history)
    {
        int historyLength = history.getHistoryLength();
        if (mode != MODE_RECONCILLIATION
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