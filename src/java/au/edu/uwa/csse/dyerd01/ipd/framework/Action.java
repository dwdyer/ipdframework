// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines valid actions for players in the IPD.  Uses Type-Safe Enum pattern
 * from Effective Java by Joshua Bloch (page 104).
 */
public final class Action
{
    private final String name;
    
    private Action(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    // The only valid actions are defined here:
    public static final Action COOPERATE = new Action("Cooperate");
    public static final Action DEFECT = new Action("Defect");
}