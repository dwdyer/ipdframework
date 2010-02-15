// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link TournamentManager} class.
 * @author Daniel Dyer
 * @since 13/5/2004
 * @version $Revision: $
 */
public class TournamentManagerTest extends TestCase
{
    public TournamentManagerTest(String name)
    {
        super(name);
    }
    
    
    public void testRoundRobinTournament()
    {
        Player[] players = {new AlwaysCooperate(), new AlwaysCooperate(), new AlwaysCooperate()};
        RoundRobinResult[] results = TournamentManager.getInstance().executeRoundRobinTournament(players, 5, 0, false);
        assertEquals(3, results.length);
        for (RoundRobinResult result : results)
        {
            // Make sure that the correct number of games have been played by each player.
            assertEquals(2, result.getHeadToHeadResultCount());
            // Make sure that each player has played each of the others and that they haven't played
            // against each other.
            assertNotSame(result.getPlayer(), result.getHeadToHeadResult(0).getOpponent());
            assertNotSame(result.getPlayer(), result.getHeadToHeadResult(1).getOpponent());
            assertNotSame(result.getHeadToHeadResult(0).getOpponent(), result.getHeadToHeadResult(1).getOpponent());
        }
    }
}
