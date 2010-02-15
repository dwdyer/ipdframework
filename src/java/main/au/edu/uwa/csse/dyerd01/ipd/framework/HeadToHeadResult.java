// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * @author Daniel Dyer
 */
public class HeadToHeadResult
{
    protected final Player player;
    private final Player opponent;
    protected int aggregatePayOff;
    protected int aggregateOpponentPayOff;
    protected int iterations;
    
    public HeadToHeadResult(Player player,
                            Player opponent,
                            int aggregatePayOff,
                            int aggregateOpponentPayOff,
                            int iterations)
    {
        this.player = player;
        this.opponent = opponent;
        this.aggregatePayOff = aggregatePayOff;
        this.aggregateOpponentPayOff = aggregateOpponentPayOff;
        this.iterations = iterations;
    }

    
    public Player getPlayer()
    {
        return player;
    }
    
    
    public Player getOpponent()
    {
        return opponent;
    }
    
    
    public int getAggregatePayOff()
    {
        return aggregatePayOff;
    }
    
    
    public int getAggregateOpponentPayOff()
    {
        return aggregateOpponentPayOff;
    }
    
    
    public int getMargin()
    {
        return aggregatePayOff - aggregateOpponentPayOff;
    }
    
    
    public int getIterations()
    {
        return iterations;
    }
    
    
    public double getAveragePayOff()
    {
        if (iterations == 0)
        {
            return 0;
        }
        return ((double) aggregatePayOff) / iterations;
    }
    
    
    public double getAverageOpponentPayOff()
    {
        if (iterations == 0)
        {
            return 0;
        }
        return ((double) aggregateOpponentPayOff) / iterations;
    }
    
    
    @Override
    public String toString()
    {
        return "vs. " + opponent.getName();
    }
}