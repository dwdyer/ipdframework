// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;

/**
 * @author Daniel Dyer
 */
public class PeriodicCCD extends AbstractPeriodicPlayer
{
    public PeriodicCCD()
    {
        super(new Action[]{Action.COOPERATE, Action.COOPERATE, Action.DEFECT});
    }
    
    public String getName()
    {
        return "PeriodicCCD";
    }
}