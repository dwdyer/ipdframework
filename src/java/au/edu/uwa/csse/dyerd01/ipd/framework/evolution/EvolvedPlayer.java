// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework.evolution;

import au.edu.uwa.csse.dyerd01.ipd.framework.AbstractPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.Action;
import au.edu.uwa.csse.dyerd01.ipd.framework.GameHistory;
import au.edu.uwa.csse.dyerd01.ipd.framework.RandomNumberGenerator;

/**
 * @author Daniel Dyer
 */
public class EvolvedPlayer extends AbstractPlayer
{
    // Mutation probability and standard deviation for manipulating fitness ratio.
    private static final double RATIO_MP = 0.1;
    private static final double RATIO_SD = 0.1;
    
    private static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    private static int instanceCount = 0;
    
    private final int id;
    private final int historyLength;
    private final double[][] cooperationProbabilities;
    
    // 0 - 1.  Zero means score against modeller is ignored, 1 means score against population is ignored.
    private double fitnessRatio = 0;
    private boolean mutateRatio = false;
    
    protected EvolvedPlayer(int historyLength)
    {
        id = getNextID();
        this.historyLength = historyLength;
        // Construct array for storing evolved probabilities.  Don't need a rectangular array
        // because the first rows and columns are for the initial moves when there isn't a
        // full history to consult so there are less combinations of possible states.
        int arrayLength = exp(2, historyLength + 1) - 1;
        cooperationProbabilities = new double[arrayLength][];
        int offset = 0;
        for (int i = 0; i <= historyLength; i++)
        {
            int count = exp(2, i);
            for (int j = 0; j < count; j++)
            {
                cooperationProbabilities[offset + j] = new double[count];
            }
            offset += count;
        }
    }
        
    
    /**
     * Recursive helper method for calculating integer exponentials.
     */
    private int exp(int base, int exponent)
    {
        return exponent == 0 ? 1 : base * exp(base, exponent - 1);
    }
    
    
    public String getName()
    {
        return "EvolvedStrategy-" + id;
    }
    
    
    public double getFitnessRatio()
    {
        return fitnessRatio;
    }
    
    
    public Action getNextMove(GameHistory history)
    {
        int playerIndex = 0;
        int opponentIndex = 0;
        // Work out indices for array based on previous moves.
        for (int i = Math.min(historyLength, history.getHistoryLength()); i > 0; i--)
        {
            int iterationNo = history.getHistoryLength() - i;
            Action playerAction = history.getPlayerActionForIteration(this, iterationNo);
            playerIndex += (playerAction == Action.COOPERATE ? 1 : 2) * exp(2, i - 1);
            Action opponentAction = history.getOpponentActionForIteration(this, iterationNo);
            opponentIndex += (opponentAction == Action.COOPERATE ? 0 : 1) * exp(2, i - 1);
        }
        // System.out.println("Looking up entry for " + playerIndex + "," + opponentIndex);
        double cooperationProbability = cooperationProbabilities[playerIndex][opponentIndex];
        if (cooperationProbability == 0 || RANDOM.nextDouble() > cooperationProbability)
        {
            return Action.DEFECT;
        }
        else
        {
            return Action.COOPERATE;
        }
    }
    
    
    /**
     * Create a mutated version of this player.  The specified <code>mutationProbability</code>
     * is used to decide, individually, whether mutation is to be performed on each of the
     * probabilities in this player's strategy.  If mutation is to occur the value is adjusted
     * by a value taken from a Gaussian distribution with the specified <code>standardDeviation</code>.
     */
    public EvolvedPlayer mutate(double mutationProbability, double standardDeviation, boolean pure)
    {
        EvolvedPlayer mutatedPlayer = new EvolvedPlayer(historyLength);
        mutatedPlayer.fitnessRatio = this.fitnessRatio;
        mutatedPlayer.mutateRatio = this.mutateRatio;
        for (int i = 0; i < this.cooperationProbabilities.length; i++)
        {
            for (int j = 0; j < this.cooperationProbabilities[i].length; j++)
            {
                mutatedPlayer.cooperationProbabilities[i][j] = this.cooperationProbabilities[i][j];
                if (RANDOM.nextDouble() <= mutationProbability)
                {
                    if (pure)
                    {
                        if (mutatedPlayer.cooperationProbabilities[i][j] == 1.0)
                        {
                            mutatedPlayer.cooperationProbabilities[i][j] = 0.0;
                        }
                        else
                        {
                            mutatedPlayer.cooperationProbabilities[i][j] = 1.0;
                        }                        
                    }
                    else
                    {
                        mutatedPlayer.cooperationProbabilities[i][j] += RANDOM.nextGaussian(0, standardDeviation);
                        // Make sure probabilities stay within the range 0-1.
                        if (mutatedPlayer.cooperationProbabilities[i][j] < 0)
                        {
                            mutatedPlayer.cooperationProbabilities[i][j] = 0;
                        }
                        else if (mutatedPlayer.cooperationProbabilities[i][j] > 1)
                        {
                            mutatedPlayer.cooperationProbabilities[i][j] = 1;
                        }
                    }
                }                
            }
        }
        
        // Mutate the fitnessRatio, if required.
        if (mutateRatio && RANDOM.nextDouble() <= RATIO_MP)
        {
            mutatedPlayer.fitnessRatio += RANDOM.nextGaussian(0, RATIO_SD);
            // Make sure it's between zero and one.
            if (mutatedPlayer.fitnessRatio < 0)
            {
                mutatedPlayer.fitnessRatio = 0;
            }
            else if (mutatedPlayer.fitnessRatio > 1)
            {
                mutatedPlayer.fitnessRatio = 1;
            }
        }
        return mutatedPlayer;
    }
    
    
    /**
     * This won't work for any history length other than 1.
     *       S
     *       t
     *       a
     *       r CCDD Player
     *       t CDCD Opponent
     *
     *  0 - (D)DDDD - Always Defect
     *  1 - (D)DDDC
     *  2 - (D)DDCD
     *  3 - (D)DDCC
     *  4 - (D)DCDD - Always Defect (Variant-4)
     *  5 - (D)DCDC
     *  6 - (D)DCCD
     *  7 - (D)DCCC
     *  8 - (D)CDDD - Always Defect (Variant-8)
     *  9 - (D)CDDC - Suspicious Pavlov
     * 10 - (D)CDCD - Suspicious Tit-For-Tat
     * 11 - (D)CDCC
     * 12 - (D)CCDD - Always Defect (Variant-12)
     * 13 - (D)CCDC
     * 14 - (D)CCCD
     * 15 - (D)CCCC - Stupid
     * 16 - (C)DDDD
     * 17 - (C)DDDC
     * 18 - (C)DDCD
     * 19 - (C)DDCC
     * 20 - (C)DCDD
     * 21 - (C)DCDC
     * 22 - (C)DCCD
     * 23 - (C)DCCC
     * 24 - (C)CDDD - Grim
     * 25 - (C)CDDC - Pavlov
     * 26 - (C)CDCD - Tit-For-Tat
     * 27 - (C)CDCC
     * 28 - (C)CCDD - Always Cooperate (Variant-28)
     * 29 - (C)CCDC - Always Cooperate (Variant-29)
     * 30 - (C)CCCD - Always Cooperate (Variant-30)
     * 31 - (C)CCCC - Always Cooperate
     */
    public int classify()
    {
        return (int) (Math.round(cooperationProbabilities[0][0]) * 16
                    + Math.round(cooperationProbabilities[1][0]) * 8
                    + Math.round(cooperationProbabilities[1][1]) * 4
                    + Math.round(cooperationProbabilities[2][0]) * 2
                    + Math.round(cooperationProbabilities[2][1]));
    }


    private static int getNextID()
    {
        return ++instanceCount;
    }
    
    
    /**
     * Generate a random player with a fixed fitness ratio.
     */    
    public static EvolvedPlayer generateRandomPlayer(int historyLength, boolean pure, double fitnessRatio)
    {
        EvolvedPlayer player = generateRandomPlayer(historyLength, pure);
        player.fitnessRatio = fitnessRatio;
        player.mutateRatio = false;
        return player;
    }

    
    /**
     * Generate a random player with a co-evolved fitness ratio.
     */
    public static EvolvedPlayer generateRandomPlayer(int historyLength, boolean pure)
    {
        EvolvedPlayer player = new EvolvedPlayer(historyLength);
        for (int i = 0; i < player.cooperationProbabilities.length; i++)
        {
            for (int j = 0; j < player.cooperationProbabilities[i].length; j++)
            {
                if (pure)
                {
                    player.cooperationProbabilities[i][j] = RANDOM.nextInt(2); // Value between 0 and 1.
                }
                else
                {
                    player.cooperationProbabilities[i][j] = RANDOM.nextDouble();
                }
            }
        }
        player.fitnessRatio = RANDOM.nextDouble();
        player.mutateRatio = true;
        return player;
    }
}
