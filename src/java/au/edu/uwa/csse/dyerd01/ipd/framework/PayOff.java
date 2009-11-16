// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines pay-offs for players in the IPD.  Uses Type-Safe Enum pattern
 * from Effective Java by Joshua Bloch (page 104).
 */
public final class PayOff
{
    // The only valid pay-offs are defined here:
    public static final PayOff EXPLOITATION_WINNER = new PayOff(5);
    public static final PayOff MUTUAL_COOPERATION = new PayOff(3);
    public static final PayOff MUTUAL_DEFECTION = new PayOff(1);
    public static final PayOff EXPLOITATION_LOSER = new PayOff(0);

    private final int value;
    
    private PayOff(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return value;
    }
    
    
    @Override
    public String toString()
    {
        return "PayOff_" + value;
    }
}