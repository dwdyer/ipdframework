// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.unittests;

import au.edu.uwa.csse.dyerd01.ipd.framework.HeadToHeadResult;
import au.edu.uwa.csse.dyerd01.ipd.framework.Player;
import au.edu.uwa.csse.dyerd01.ipd.framework.RoundRobinResult;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import java.util.Comparator;
import junit.framework.TestCase;

/**
 * JUnit unit test for the {@link RoundRobinResult} class.
 * @author Daniel Dyer
 * @since 12/5/2004
 * @version $Revision: $
 */
public class RoundRobinResultTest extends TestCase
{
    public RoundRobinResultTest(String name)
    {
        super(name);
    }
    
    
    public void testResultCount()
    {
        Player player = new AlwaysCooperate();
        RoundRobinResult result = new RoundRobinResult(player);
        assertEquals(0, result.getHeadToHeadResultCount());
        HeadToHeadResult headToHead = new HeadToHeadResult(player, new AlwaysCooperate(), 10, 40, 10);
        result.addHeadToHeadResult(headToHead);
        assertEquals(1, result.getHeadToHeadResultCount());
    }
    
    
    public void testResultRetrieval()
    {
        Player player = new AlwaysCooperate();
        RoundRobinResult result = new RoundRobinResult(player);
        HeadToHeadResult headToHead1 = new HeadToHeadResult(player, new AlwaysCooperate(), 10, 40, 10);
        HeadToHeadResult headToHead2 = new HeadToHeadResult(player, new AlwaysCooperate(), 40, 10, 10);
        result.addHeadToHeadResult(headToHead1);
        result.addHeadToHeadResult(headToHead2);
        assertSame(headToHead1, result.getHeadToHeadResult(0));
        assertSame(headToHead2, result.getHeadToHeadResult(1));
    }
    
    
    public void testAggregatesAndAverages()
    {
        Player player = new AlwaysCooperate();
        RoundRobinResult result = new RoundRobinResult(player);
        // Test that an empty set of results returns the appropriate zero values.
        assertEquals(0, result.getAggregatePayOff());
        assertEquals(0, result.getAggregateOpponentPayOff());
        assertEquals(0, result.getIterations());
        assertEquals(0, result.getMargin());
        assertTrue(result.getAveragePayOff() == 0);
        // Test calculations with a single entry.
        HeadToHeadResult headToHead = new HeadToHeadResult(player, new AlwaysCooperate(), 15, 40, 10);
        result.addHeadToHeadResult(headToHead);
        assertEquals(15, result.getAggregatePayOff());
        assertEquals(40, result.getAggregateOpponentPayOff());
        assertEquals(10, result.getIterations());
        assertEquals(-25, result.getMargin());
        assertTrue(result.getAveragePayOff() == 1.5);
        // Test calculations with multiple entries.
        headToHead = new HeadToHeadResult(player, new AlwaysCooperate(), 45, 5, 20);
        result.addHeadToHeadResult(headToHead);
        assertEquals(60, result.getAggregatePayOff());
        assertEquals(45, result.getAggregateOpponentPayOff());
        assertEquals(30, result.getIterations());
        assertEquals(15, result.getMargin());
        assertTrue(result.getAveragePayOff() == 2);
    }
    
    
    public void testSort()
    {
        Player player = new AlwaysCooperate();
        RoundRobinResult result = new RoundRobinResult(player);
        // The actual comparator used is not important.
        Comparator<HeadToHeadResult> headToHeadComparator = new Comparator<HeadToHeadResult>()
        {
            public int compare(HeadToHeadResult result1, HeadToHeadResult result2)
            {
                // Sort by aggregate pay-off, doesn't really matter which field we choose.
                return result1.getAggregatePayOff() - result2.getAggregatePayOff();
            }
        };
        // Sort the empty set just to confirm that there are no exceptions.
        result.sortResults(headToHeadComparator);
        
        // Sort some results and confirm that the order is as expected.
        HeadToHeadResult headToHead1 = new HeadToHeadResult(player, new AlwaysCooperate(), 40, 10, 10);       
        HeadToHeadResult headToHead2 = new HeadToHeadResult(player, new AlwaysCooperate(), 10, 10, 10);       
        HeadToHeadResult headToHead3 = new HeadToHeadResult(player, new AlwaysCooperate(), 30, 10, 10);
        result.addHeadToHeadResult(headToHead1);
        result.addHeadToHeadResult(headToHead2);
        result.addHeadToHeadResult(headToHead3);
        result.sortResults(headToHeadComparator);
        assertSame(headToHead2, result.getHeadToHeadResult(0));
        assertSame(headToHead3, result.getHeadToHeadResult(1));
        assertSame(headToHead1, result.getHeadToHeadResult(2));
    }
}
