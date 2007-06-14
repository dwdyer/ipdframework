// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.strategies;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class PunitiveOpponentModel extends LookUpTableOpponentModel
{
    private static final Logger logger = Logger.getLogger(PunitiveOpponentModel.class);
    
    private static final Action[][][][][] LOOK_UP_TABLE = new Action[10][10][10][10][];
    static
    {
        logger.debug("Pre-computing best responses...");
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                for (int k = 0; k < 10; k++)
                {
                    for (int l = 0; l < 10; l++)
                    {
                        LOOK_UP_TABLE[i][j][k][l] = getBestResponse(((double) i) / 10 + 0.05,
                                                                    ((double) j) / 10 + 0.05,
                                                                    ((double) k) / 10 + 0.05,
                                                                    ((double) l) / 10 + 0.05);
                    }
                }
            }
            logger.debug((i + 1) * 10 + "% done.");
        }
    }
    
    private static Action[] getBestResponse(double cc, double cd, double dc, double dd)
    {
        Action[] bestResponse = new Action[4];
        double cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.COOPERATE, Action.COOPERATE, LOOK_AHEAD);
        double defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.COOPERATE, Action.DEFECT, LOOK_AHEAD);
        bestResponse[0] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.DEFECT, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.COOPERATE, Action.DEFECT, Action.DEFECT, LOOK_AHEAD);
        bestResponse[1] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.COOPERATE, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.COOPERATE, Action.DEFECT, LOOK_AHEAD);        
        bestResponse[2] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;
        
        cooperatePayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.DEFECT, Action.COOPERATE, LOOK_AHEAD);
        defectPayOff = calculatePayOff(cc, cd, dc, dd, Action.DEFECT, Action.DEFECT, Action.DEFECT, LOOK_AHEAD);
        bestResponse[3] = cooperatePayOff >= defectPayOff ? Action.COOPERATE : Action.DEFECT;        

        return bestResponse;
    }
    
    private static double calculatePayOff(double cc,
                                          double cd,
                                          double dc,
                                          double dd,
                                          Action playerLastMove,
                                          Action opponentLastMove,
                                          Action chosenMove,
                                          int lookAhead)
    {
        if (lookAhead == 0)
        {
            return 0;
        }
        
        double cooperateProbability = 0;
        if (playerLastMove == Action.COOPERATE)
        {
            cooperateProbability = opponentLastMove == Action.COOPERATE ? cc : cd;
        }
        else
        {
            cooperateProbability = opponentLastMove == Action.COOPERATE ? dc : dd;
        }

        
        if (chosenMove == null)
        {
            double cooperatePayOff = Math.max(cooperateProbability * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1),
                                              (PayOff.EXPLOITATION_LOSER.getValue() - PayOff.EXPLOITATION_WINNER.getValue()) + (1 - cooperateProbability) * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
            double defectPayOff = Math.max((PayOff.EXPLOITATION_WINNER.getValue() - PayOff.EXPLOITATION_LOSER.getValue()) + cooperateProbability * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1),
                                           (1 - cooperateProbability) * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
            return Math.max(cooperatePayOff, defectPayOff);
        }
        if (chosenMove == Action.COOPERATE)
        {
            return Math.max(cooperateProbability * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1),
                            (PayOff.EXPLOITATION_LOSER.getValue() - PayOff.EXPLOITATION_WINNER.getValue()) + (1 - cooperateProbability) * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
        }
        else
        {
            return Math.max((PayOff.EXPLOITATION_WINNER.getValue() - PayOff.EXPLOITATION_LOSER.getValue()) + cooperateProbability * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.COOPERATE, null, lookAhead - 1),
                            (1 - cooperateProbability) * calculatePayOff(cc, cd, dc, dd, chosenMove, Action.DEFECT, null, lookAhead - 1));
        }
    }

    
    /**
     * Creates a new opponent model with the specified look-back size for the specified player
     * (NOTE: it does not model the specified player, but models that player's opponent.  The player
     * reference here is for extracting information from the game history).
     */
    public PunitiveOpponentModel(Player player)
    {
        super(player);
    }
}