// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link HeadToHeadResult} class.
 * @author Daniel Dyer
 * @since 11/5/2004
 * @version $Revision: $
 */
public class HeadToHeadResultTest extends TestCase
{
    public HeadToHeadResultTest(String name)
    {
        super(name);
    }


    /**
     * Test that the margin is correctly calculated.
     */
    public void testMargin()
    {
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        HeadToHeadResult result = new HeadToHeadResult(player1, player2, 10, 40, 10);
        assertEquals(-30, result.getMargin());
        result = new HeadToHeadResult(player1, player2, 50, 40, 10);
        assertEquals(10, result.getMargin());
        result = new HeadToHeadResult(player1, player2, 20, 20, 10);
        assertEquals(0, result.getMargin());
    }
    
    
    /**
     * Test that the average is correctly calculated.
     */
    public void testAverage()
    {
        Player player1 = new AlwaysCooperate();
        Player player2 = new AlwaysCooperate();
        HeadToHeadResult result = new HeadToHeadResult(player1, player2, 15, 40, 10);
        assertTrue(result.getAveragePayOff() == 1.5);
        result = new HeadToHeadResult(player1, player2, 0, 40, 10);
        assertTrue(result.getAveragePayOff() == 0);
    }
}
