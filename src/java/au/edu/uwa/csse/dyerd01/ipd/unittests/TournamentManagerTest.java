// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import java.util.Comparator;
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
        Player[] players = new Player[]{new AlwaysCooperate(), new AlwaysCooperate(), new AlwaysCooperate()};
        RoundRobinResult[] results = TournamentManager.getInstance().executeRoundRobinTournament(players, 5, 0, false);
        assertEquals(3, results.length);
        for (int i = 0; i < results.length; i++)
        {
            // Make sure that the correct number of games have been played by each player.
            assertEquals(2, results[i].getHeadToHeadResultCount());
            // Make sure that each player has played each of the others and that they haven't played
            // against each other.
            assertNotSame(results[i].getPlayer(), results[i].getHeadToHeadResult(0).getOpponent());
            assertNotSame(results[i].getPlayer(), results[i].getHeadToHeadResult(1).getOpponent());
            assertNotSame(results[i].getHeadToHeadResult(0).getOpponent(), results[i].getHeadToHeadResult(1).getOpponent());
        }
    }
}
