// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

/**
 * @author Daniel Dyer
 */
public abstract class AbstractOpponentModeller extends AbstractPlayer
{
    protected OpponentModel model = null;
    
    public void reset()
    {
        model = createNewOpponentModel(TournamentManager.HISTORY_LENGTH);
    }

    
    public Action getNextMove(GameHistory history)
    {
        if (history.getHistoryLength() > 0)
        {
            model.updateModel(history);
        }
        return model.getBestResponse(history);
    }
    
    
    protected abstract OpponentModel createNewOpponentModel(int historyLength);
}