// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.Action;

/**
 * @author Daniel Dyer
 */
public class PeriodicDDC extends AbstractPeriodicPlayer
{
    public PeriodicDDC()
    {
        super(new Action[]{Action.DEFECT, Action.DEFECT, Action.COOPERATE});
    }
    
    public String getName()
    {
        return "PeriodicDDC";
    }
}