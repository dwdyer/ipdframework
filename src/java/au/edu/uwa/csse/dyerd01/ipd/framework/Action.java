// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * Defines valid actions for players in the IPD.
 */
public enum Action
{
    COOPERATE("Cooperate"),
    DEFECT("Defect");

    private final String name;
    
    Action(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}