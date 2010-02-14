// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines pay-offs for players in the IPD.
 */
public enum PayOff
{
    // The only valid pay-offs are defined here:
    EXPLOITATION_WINNER(5),
    MUTUAL_COOPERATION(3),
    MUTUAL_DEFECTION(1),
    EXPLOITATION_LOSER(0);

    private final int value;
    
    PayOff(int value)
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