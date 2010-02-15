// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class HeadToHead implements Runnable
{
    private static final Logger logger = Logger.getLogger(HeadToHead.class);
    
    private final Player player1;
    private final Player player2;
    private final int noOfRounds;
    private final double noiseProbability;
    private final GameHistory history;
    
    /**
     * Executes a head-to-head game between two players for the specified number of
     * rounds with zero probability of noise.
     */
    public HeadToHead(Player player1, Player player2, int noOfRounds)
    {
        this(player1, player2, noOfRounds, 0);
    }
    

    /**
     * Executes a head-to-head game between two players for the specified number of
     * rounds with the specified probability of noise.
     */
    public HeadToHead(Player player1, Player player2, int noOfRounds, double noiseProbability)
    {
        this.player1 = player1;
        this.player2 = player2;
        this.noOfRounds = noOfRounds;
        this.noiseProbability = noiseProbability;
        this.history = new GameHistory(player1, player2);
    }
    
    
    public void run()
    {
        logger.debug(player1 + " vs. " + player2 + " (" + noOfRounds + " iterations):");
        player1.reset();
        player2.reset();
        for (int i = 0; i < noOfRounds; i++)
        {
            Action player1Action = noisyMove(player1.getNextMove(history));
            Action player2Action = noisyMove(player2.getNextMove(history));
            history.addIteration(player1Action, player2Action);
        }
        logger.debug("Aggregate pay-offs: " + player1 + " " + history.getAggregatePayOffForPlayer(player1)
                     + " - " + player2 + " " + history.getAggregatePayOffForPlayer(player2));
    }
    
    
    /**
     * Uses the specified noise probability to decide whether to change the action from its
     * intended value.
     */
    private Action noisyMove(Action move)
    {
        if (1 - Math.random() <= noiseProbability)
        {
            logger.debug("Noisy move.");
            return move == Action.COOPERATE ? Action.DEFECT : Action.COOPERATE;
        }
        return move;
    }
    
    
    public GameHistory getHistory()
    {
        return history;
    }
}