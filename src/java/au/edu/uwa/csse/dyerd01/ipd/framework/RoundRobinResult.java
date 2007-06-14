// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Dyer
 */
public class RoundRobinResult extends HeadToHeadResult
{
    private final List<HeadToHeadResult> headToHeadResults = new ArrayList<HeadToHeadResult>();
    private final Map<Player, HeadToHeadResult> opponentMap = new HashMap<Player, HeadToHeadResult>();
    
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
        return headToHeadResults.get(index);
    }
    
    
    public HeadToHeadResult getHeadToHeadResult(Player opponent)
    {
        return opponentMap.get(opponent);
    }
    
    
    public void sortResults(Comparator<HeadToHeadResult> comparator)
    {
        Collections.sort(headToHeadResults, comparator);
    }
    
    
    public String toString()
    {
        return player.getName();
    }
}