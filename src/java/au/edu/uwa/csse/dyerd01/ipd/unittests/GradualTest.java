// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysDefect;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Gradual;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link Gradual} strategy implementation.
 * @author Daniel Dyer
 * @since 13/5/2004
 * @version $Revision: $
 */
public class GradualTest extends TestCase
{
    public GradualTest(String name)
    {
        super(name);
    }
    
    
    public void testCooperation()
    {
        Gradual gradual = new Gradual();
        GameHistory history = new GameHistory(gradual, new AlwaysCooperate());
        for (int i = 0; i < 3; i++)
        {
            assertEquals(Action.COOPERATE, gradual.getNextMove(history));
            history.addIteration(Action.COOPERATE, Action.COOPERATE);
        }
    }
    
    
    public void testPunishment()
    {
        Gradual gradual = new Gradual();
        GameHistory history = new GameHistory(gradual, new AlwaysDefect());
        history.addIteration(Action.COOPERATE, Action.DEFECT);
        for (int i = 0; i < 3; i++)
        {
            System.out.println("Cycle no. " + i );
            for (int j = 0; j < i+1; j++)
            {
                System.out.println("Checking for a defection...");
                Action nextMove = gradual.getNextMove(history);
                assertEquals(Action.DEFECT, nextMove);
                history.addIteration(nextMove, Action.DEFECT);
            }
            // Test cooperation after punishment.
            Action nextMove = gradual.getNextMove(history);
            assertEquals(Action.COOPERATE, nextMove);
            history.addIteration(nextMove, Action.DEFECT);
            nextMove = gradual.getNextMove(history);
            assertEquals(Action.COOPERATE, nextMove);
            history.addIteration(nextMove, Action.DEFECT);
        }        
    }
}
