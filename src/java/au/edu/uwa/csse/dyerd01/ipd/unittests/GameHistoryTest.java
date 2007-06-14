// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link GameHistory} class.
 * @author Daniel Dyer
 * @since 11/5/2004
 * @version $Revision: $
 */
public class GameHistoryTest extends TestCase
{
    public GameHistoryTest(String name)
    {
        super(name);
    }


    /**
     * Tests that the history reports the correct size, both when empty and with entries.
     */
    public void testHistoryLength()
    {
        GameHistory history = new GameHistory(new AlwaysCooperate(), new AlwaysCooperate());
        assertEquals(0, history.getHistoryLength());
        history.addIteration(Action.COOPERATE, Action.COOPERATE);
        assertEquals(1, history.getHistoryLength());
        history.addIteration(Action.COOPERATE, Action.COOPERATE);
        assertEquals(2, history.getHistoryLength());
    }
    
    
    /**
     * Tests that previous Actions are correctly stored and retrieved from the history.
     */
    public void testActionRetrieval()
    {
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        GameHistory history = new GameHistory(player1, player2);
        history.addIteration(Action.COOPERATE, Action.DEFECT);
        history.addIteration(Action.DEFECT, Action.COOPERATE);
        // Test for specified player.
        assertEquals(Action.COOPERATE, history.getPlayerActionForIteration(player1, 0));
        assertEquals(Action.DEFECT, history.getPlayerActionForIteration(player2, 0));
        assertEquals(Action.DEFECT, history.getPlayerActionForIteration(player1, 1));
        assertEquals(Action.COOPERATE, history.getPlayerActionForIteration(player2, 1));
        // Test for specified player's opponent.
        assertEquals(Action.DEFECT, history.getOpponentActionForIteration(player1, 0));
        assertEquals(Action.COOPERATE, history.getOpponentActionForIteration(player2, 0));
        assertEquals(Action.COOPERATE, history.getOpponentActionForIteration(player1, 1));
        assertEquals(Action.DEFECT, history.getOpponentActionForIteration(player2, 1));
    }
    
    
    /**
     * Tests that pay-offs are correctly assigned and retrieved for all combinations
     * of COOPERATE and DEFECT.
     */
    public void testPayOffs()
    {
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        GameHistory history = new GameHistory(player1, player2);
        history.addIteration(Action.COOPERATE, Action.COOPERATE);
        history.addIteration(Action.COOPERATE, Action.DEFECT);
        history.addIteration(Action.DEFECT, Action.DEFECT);
        // Test pay-offs for specified player.
        assertEquals(PayOff.MUTUAL_COOPERATION, history.getPlayerPayOffForIteration(player1, 0));
        assertEquals(PayOff.MUTUAL_COOPERATION, history.getPlayerPayOffForIteration(player2, 0));
        assertEquals(PayOff.EXPLOITATION_LOSER, history.getPlayerPayOffForIteration(player1, 1));
        assertEquals(PayOff.EXPLOITATION_WINNER, history.getPlayerPayOffForIteration(player2, 1));
        assertEquals(PayOff.MUTUAL_DEFECTION, history.getPlayerPayOffForIteration(player1, 2));
        assertEquals(PayOff.MUTUAL_DEFECTION, history.getPlayerPayOffForIteration(player2, 2));
        // Test pay-offs for specified player's opponent.
        assertEquals(PayOff.MUTUAL_COOPERATION, history.getOpponentPayOffForIteration(player1, 0));
        assertEquals(PayOff.MUTUAL_COOPERATION, history.getOpponentPayOffForIteration(player2, 0));
        assertEquals(PayOff.EXPLOITATION_WINNER, history.getOpponentPayOffForIteration(player1, 1));
        assertEquals(PayOff.EXPLOITATION_LOSER, history.getOpponentPayOffForIteration(player2, 1));
        assertEquals(PayOff.MUTUAL_DEFECTION, history.getOpponentPayOffForIteration(player1, 2));
        assertEquals(PayOff.MUTUAL_DEFECTION, history.getOpponentPayOffForIteration(player2, 2));
    }
    
    
    /**
     * Tests the calculation of aggregate pay-offs and margins for each player.
     */
    public void testAggregates()
    {
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        GameHistory history = new GameHistory(player1, player2);
        history.addIteration(Action.COOPERATE, Action.COOPERATE);
        history.addIteration(Action.COOPERATE, Action.DEFECT);
        history.addIteration(Action.DEFECT, Action.DEFECT);
        int expectedPlayer1PayOff = PayOff.MUTUAL_COOPERATION.getValue() + PayOff.EXPLOITATION_LOSER.getValue() + PayOff.MUTUAL_DEFECTION.getValue();
        int expectedPlayer2PayOff = PayOff.MUTUAL_COOPERATION.getValue() + PayOff.EXPLOITATION_WINNER.getValue() + PayOff.MUTUAL_DEFECTION.getValue();
        assertEquals(expectedPlayer1PayOff, history.getAggregatePayOffForPlayer(player1));
        assertEquals(expectedPlayer2PayOff, history.getAggregatePayOffForPlayer(player2));
        assertEquals(expectedPlayer1PayOff - expectedPlayer2PayOff, history.getAggregateMarginForPlayer(player1));
        assertEquals(expectedPlayer2PayOff - expectedPlayer1PayOff, history.getAggregateMarginForPlayer(player2));
    }
}
