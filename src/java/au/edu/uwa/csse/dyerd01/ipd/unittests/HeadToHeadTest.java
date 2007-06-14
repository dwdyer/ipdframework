// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.HeadToHead;
import au.edu.uwa.csse.dyerd01.ipd.framework.PayOff;
import au.edu.uwa.csse.dyerd01.ipd.framework.Player;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link HeadToHead} class.
 * @author Daniel Dyer
 * @since 13/5/2004
 * @version $Revision: $
 */
public class HeadToHeadTest extends TestCase
{
    public HeadToHeadTest(String name)
    {
        super(name);
    }
    

    /**
     * Plays two ALWAYS_COOPERATE strategies against each other and checks that the
     * outcome, with no noise, is as expected.
     */
    public void testWithNoNoise()
    {
        int noRounds = 10;
        int expectedPayOff = PayOff.MUTUAL_COOPERATION.getValue() * noRounds;
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        HeadToHead game = new HeadToHead(player1, player2, noRounds);
        game.run();
        GameHistory history = game.getHistory();
        assertEquals(noRounds, history.getHistoryLength());
        assertEquals(expectedPayOff, history.getAggregatePayOffForPlayer(player1));
        assertEquals(expectedPayOff, history.getAggregatePayOffForPlayer(player2));
    }
    

    /**
     * Plays two ALWAYS_COOPERATE strategies against each other with a noise probability
     * of 1 (so that every move is noisy).  Asserts that the outcome is as expected
     * (every move by both players is DEFECT).
     */
    public void testWithNoise()
    {
        int noRounds = 5;
        double noisProbability = 1.0;
        int expectedPayOff = PayOff.MUTUAL_DEFECTION.getValue() * noRounds;
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        HeadToHead game = new HeadToHead(player1, player2, noRounds, noisProbability);
        game.run();
        GameHistory history = game.getHistory();
        assertEquals(noRounds, history.getHistoryLength());
        assertEquals(expectedPayOff, history.getAggregatePayOffForPlayer(player1));
        assertEquals(expectedPayOff, history.getAggregatePayOffForPlayer(player2));
    }
}
