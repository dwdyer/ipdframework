// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the full history of any previous iterations of a single head-to-head IPD
 * game between two players.
 * @author Daniel Dyer
 */
public class GameHistory
{
    private final Player player1;
    private final Player player2;
    private List<HistoryEntry> previousIterations = new ArrayList<HistoryEntry>();
    private int player1AggregatePayOff;
    private int player2AggregatePayOff;
    

    /**
     * Creates an initially empty history object for recording the actions of
     * an IPD head-to-head between the two specified players.
     */
    public GameHistory(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;
    }
    
    
    /**
     * Updates the history with the data from the most recent iteration.  At present
     * there is nothing to prevent a malicious strategy from cheating by calling this
     * method to corrupt the history data.
     */
    public void addIteration(Action player1Action, Action player2Action)
    {
        HistoryEntry historyEntry = new HistoryEntry(player1Action, player2Action);
        previousIterations.add(historyEntry);
        player1AggregatePayOff += historyEntry.player1PayOff.getValue();
        player2AggregatePayOff += historyEntry.player2PayOff.getValue();
    }


    /**
     * @return The number of iterations previously played between these two players in this game.
     */
    public int getHistoryLength()
    {
        return previousIterations.size();
    }
    

    /**
     * Get the Action played by the specified player in the specified round
     * of the game.
     */
    public Action getPlayerActionForIteration(Player player, int iterationNo)
    {
        HistoryEntry iteration = previousIterations.get(iterationNo);
        if (player == player1)
        {
            return iteration.player1Action;
        }
        else if (player == player2)
        {
            return iteration.player2Action;
        }
        else
        {
            throw new IllegalArgumentException("Unknown player specified.");
        }
    }

    
    /**
     * Get the Action played by the specified player's opponent in the specified round
     * of the game.  Note that the action returned is not for the player that is passed
     * in as a parameter.  This is because the player does not have a reference to its
     * opponent for security reasons.
     */
    public Action getOpponentActionForIteration(Player player, int iterationNo)
    {
        // Work out who the player's opponent is and delegate to the getPlayerActionForIteration
        // method.
        if (player == player1)
        {
            return getPlayerActionForIteration(player2, iterationNo);
        }
        else if (player == player2)
        {
            return getPlayerActionForIteration(player1, iterationNo);
        }
        else
        {
            throw new IllegalArgumentException("Unknown player specified.");
        }
    }
    
    
    public PayOff getPlayerPayOffForIteration(Player player, int iterationNo)
    {
        HistoryEntry iteration = previousIterations.get(iterationNo);
        if (player == player1)
        {
            return iteration.player1PayOff;
        }
        else if (player == player2)
        {
            return iteration.player2PayOff;
        }
        else
        {
            throw new IllegalArgumentException("Unknown player specified.");
        }
    }
    
    
    /**
     * Get the pay-off for the specified player's opponent in the specified round
     * of the game.  Note that the pay-off returned is not for the player that is passed
     * in as a parameter.  This is because the player does not have a reference to its
     * opponent for security reasons.
     */
    public PayOff getOpponentPayOffForIteration(Player player, int iterationNo)
    {
        // Work out who the player's opponent is and delegate to the getPlayerPayOffForIteration
        // method.
        if (player == player1)
        {
            return getPlayerPayOffForIteration(player2, iterationNo);
        }
        else if (player == player2)
        {
            return getPlayerPayOffForIteration(player1, iterationNo);
        }
        else
        {
            throw new IllegalArgumentException("Unknown player specified.");
        }
    }
    
    
    /**
     * @return The sum of the specified player's pay-offs for each iteration of
     * the game played so far.
     */
    public int getAggregatePayOffForPlayer(Player player)
    {
        return player == player1 ? player1AggregatePayOff : player2AggregatePayOff;
    }
    
    
    /**
     * Returns the difference between the specified player's aggregate pay-off and its
     * opponent's specified aggregate pay-off.  A positive value indicates that the player
     * out-performed its opponent whilst a negative value indicates that the opponent
     * performed better.
     */
    public int getAggregateMarginForPlayer(Player player)
    {
        return player == player1 ? player1AggregatePayOff - player2AggregatePayOff : player2AggregatePayOff - player1AggregatePayOff;
    }
    
    
    public GameHistory getPlayer1History()
    {
        GameHistory clone = new GameHistory(player1, null);
        clone.previousIterations = new ArrayList<HistoryEntry>(previousIterations);
        return clone;
    }

    
    public GameHistory getPlayer2History()
    {
        GameHistory clone = new GameHistory(null, player2);
        clone.previousIterations = new ArrayList<HistoryEntry>(previousIterations);
        return clone;
    }
    
 
    /**
     * Simple immutable class that encapsulates the result of a single iteration of the IPD game.
     */
    private static final class HistoryEntry
    {
        public final Action player1Action;
        public final Action player2Action;
        public final PayOff player1PayOff;
        public final PayOff player2PayOff;
        
        public HistoryEntry(Action player1Action, Action player2Action)
        {
            this.player1Action = player1Action;
            this.player2Action = player2Action;
            
            // Calculate pay-offs for each player.
            if (player1Action == player2Action)
            {
                player1PayOff = (player1Action == Action.COOPERATE) ? PayOff.MUTUAL_COOPERATION : PayOff.MUTUAL_DEFECTION;
                player2PayOff = player1PayOff;
            }
            else if (player1Action == Action.COOPERATE)
            {
                player1PayOff = PayOff.EXPLOITATION_LOSER;
                player2PayOff = PayOff.EXPLOITATION_WINNER;
            }
            else
            {
                player1PayOff = PayOff.EXPLOITATION_WINNER;
                player2PayOff = PayOff.EXPLOITATION_LOSER;
            }
        }
    }
}