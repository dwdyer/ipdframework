// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Daniel Dyer
 */
public class RoundRobinResult extends HeadToHeadResult
{
    private final ArrayList headToHeadResults = new ArrayList();
    private final HashMap opponentMap = new HashMap();
    
    public RoundRobinResult(Player player)
    {
        super(player, null, 0, 0, 0);
    }
    
    
    public void addHeadToHeadResult(HeadToHeadResult result)
    {
        headToHeadResults.add(result);
        opponentMap.put(result.getOpponent(), result);
        aggregatePayOff += result.getAggregatePayOff();
        aggregateOpponentPayOff += result.getAggregateOpponentPayOff();
        iterations += result.getIterations();
    }
    
    
    public int getHeadToHeadResultCount()
    {
        return headToHeadResults.size();
    }
    
    
    public HeadToHeadResult getHeadToHeadResult(int index)
    {
        return (HeadToHeadResult) headToHeadResults.get(index);
    }
    
    
    public HeadToHeadResult getHeadToHeadResult(Player opponent)
    {
        return (HeadToHeadResult) opponentMap.get(opponent);
    }
    
    
    public void sortResults(Comparator comparator)
    {
        Collections.sort(headToHeadResults, comparator);
    }
    
    
    public String toString()
    {
        return player.getName();
    }
}