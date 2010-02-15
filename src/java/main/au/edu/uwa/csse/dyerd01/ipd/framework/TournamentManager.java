// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionListener;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolvedPlayer;
import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.Evolver;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysCooperate;
import au.edu.uwa.csse.dyerd01.ipd.strategies.AlwaysDefect;
import au.edu.uwa.csse.dyerd01.ipd.strategies.ApproximatingOpponentModeller;
import au.edu.uwa.csse.dyerd01.ipd.strategies.DeferredTitForTat;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Gradual;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Grim;
import au.edu.uwa.csse.dyerd01.ipd.strategies.HarshGradual;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Majority;
import au.edu.uwa.csse.dyerd01.ipd.strategies.ModellerNemesis;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Pavlov;
import au.edu.uwa.csse.dyerd01.ipd.strategies.PeriodicCCD;
import au.edu.uwa.csse.dyerd01.ipd.strategies.PeriodicDDC;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Prober;
import au.edu.uwa.csse.dyerd01.ipd.strategies.PunitiveOpponentModeller;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Random;
import au.edu.uwa.csse.dyerd01.ipd.strategies.SimpleOpponentModeller;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Strategy18;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Strategy27;
import au.edu.uwa.csse.dyerd01.ipd.strategies.Strategy30;
import au.edu.uwa.csse.dyerd01.ipd.strategies.SuspiciousTitForTat;
import au.edu.uwa.csse.dyerd01.ipd.strategies.TitFor2Tats;
import au.edu.uwa.csse.dyerd01.ipd.strategies.TitForTat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Class responsible for running and controlling tournaments.
 * Implements the singleton pattern.
 * @author Daniel Dyer
 */
public class TournamentManager
{
    /**
     * Singleton instance, the only instance of this class ever instantiated.
     */
    private static final TournamentManager INSTANCE = new TournamentManager();
    
    private static final Logger logger = Logger.getLogger(TournamentManager.class);

    public static final int HISTORY_LENGTH = 1;
    private static final List<Class<? extends Player>> AVAILABLE_STRATEGIES
        = Arrays.<Class<? extends Player>>asList(AlwaysCooperate.class,
                                                 AlwaysDefect.class,
                                                 ApproximatingOpponentModeller.class,
                                                 DeferredTitForTat.class,
                                                 Gradual.class,
                                                 Grim.class,
                                                 HarshGradual.class,
                                                 Majority.class,
                                                 ModellerNemesis.class,
                                                 Pavlov.class,
                                                 PeriodicCCD.class,
                                                 PeriodicDDC.class,
                                                 Prober.class,
                                                 PunitiveOpponentModeller.class,
                                                 Random.class,
                                                 SimpleOpponentModeller.class,
                                                 Strategy18.class,
                                                 Strategy27.class,
                                                 Strategy30.class,
                                                 SuspiciousTitForTat.class,
                                                 TitForTat.class,
                                                 TitFor2Tats.class);

    /**
     * Private constructor, prevents direct instantiation.
     */
    private TournamentManager()
    {
        // Do nothing.
    }
    
    public static TournamentManager getInstance()
    {
        return INSTANCE;
    }
    
    
    public List<Class<? extends Player>> getAvailableStrategies()
    {
        return AVAILABLE_STRATEGIES;
    }
    
    
    /**
     * @param playSelf Should the strategies compete against themselves as well as the other opponents?
     */
    public RoundRobinResult[] executeRoundRobinTournament(Player[] players,
                                                          int noOfRounds,
                                                          double noiseProbability,
                                                          boolean playSelf)
    {
        logger.debug("Starting round-robin tournament with " + players.length + " players...");
        Map<Player, RoundRobinResult> playerRecords = new HashMap<Player, RoundRobinResult>();
        for (int i = 0; i < players.length - 1; i++)
        {
            for (int j = i + 1; j < players.length; j++)
            {
                playHeadToHead(players[i], players[j], noOfRounds, noiseProbability, playerRecords);
            }
        }
        
        if (playSelf)
        {
            for (int i = 0; i < players.length; i++)
            {
                try
                {
                    Player player2 = players[i].getClass().newInstance();
                    playHeadToHead(players[i], player2, noOfRounds, noiseProbability, playerRecords);
                    playerRecords.remove(player2); // Ugly hack, fix this.
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    // TO DO: Proper exception handling.
                }
            }
        }

        logger.debug("Tournament completed.");
        return playerRecords.values().toArray(new RoundRobinResult[playerRecords.size()]);
    }
    
    
    /**
     * Execute evolution using a co-evolved fitness ratio.
     */
    public EvolutionResult[] executeEvolution(boolean pureStrategiesOnly,
                                              int noOfGenerations,
                                              int populationSize,
                                              double mutationProbability,
                                              double standardDeviation,
                                              int noOfRounds,
                                              double noiseProbability,
                                              boolean playSelf,
                                              Player fixedOpponent,
                                              EvolutionListener listener)
    {
        logger.info("Tournament manager starting evolver with co-evolved ratios...");
        Evolver evolver = new Evolver(pureStrategiesOnly,
                                      HISTORY_LENGTH,
                                      noOfGenerations,
                                      populationSize,
                                      mutationProbability,
                                      standardDeviation,
                                      noOfRounds,
                                      noiseProbability,
                                      playSelf,
                                      fixedOpponent);
        if (listener != null)
        {
            evolver.addEvolutionListener(listener);
        }
        EvolutionResult[] results = evolver.start();
        if (listener != null)
        {
            evolver.removeEvolutionListener(listener);
        }
        return results;
    }
    

    /**
     * Execute evolution using a fixed fitness ratio.
     */
    public EvolutionResult[] executeEvolution(boolean pureStrategiesOnly,
                                              int noOfGenerations,
                                              int populationSize,
                                              double mutationProbability,
                                              double standardDeviation,
                                              int noOfRounds,
                                              double noiseProbability,
                                              boolean playSelf,
                                              Player fixedOpponent,
                                              int fixedPlayerFitnessFactor,
                                              EvolutionListener listener)
    {
        logger.info("Tournament manager starting evolver with fixed ratio...");
        Evolver evolver = new Evolver(pureStrategiesOnly,
                                      HISTORY_LENGTH,
                                      noOfGenerations,
                                      populationSize,
                                      mutationProbability,
                                      standardDeviation,
                                      noOfRounds,
                                      noiseProbability,
                                      playSelf,
                                      fixedOpponent,
                                      fixedPlayerFitnessFactor);
        if (listener != null)
        {
            evolver.addEvolutionListener(listener);
        }
        EvolutionResult[] results = evolver.start();
        if (listener != null)
        {
            evolver.removeEvolutionListener(listener);
        }
        return results;
    }
    
    
    public RoundRobinResult executeFixedOpponentAgainstEvolvedPopulation(Player fixedOpponent,
                                                                         EvolvedPlayer[] population,
                                                                         int noOfRounds,
                                                                         double noiseProbability)
    {
        RoundRobinResult result = new RoundRobinResult(fixedOpponent);
        for (int i = 0; i < population.length; i++)
        {
            HeadToHead game = new HeadToHead(fixedOpponent, population[i], noOfRounds, noiseProbability);
            game.run();
            int player1Aggregate = game.getHistory().getAggregatePayOffForPlayer(fixedOpponent);
            int player2Aggregate = game.getHistory().getAggregatePayOffForPlayer(population[i]);
            result.addHeadToHeadResult(new HeadToHeadResult(fixedOpponent,
                                                            population[i],
                                                            player1Aggregate,
                                                            player2Aggregate,
                                                            noOfRounds));
        }
        return result;
    }
    
    
    /**
     * Helper method for executing a single head-to-head and collating the results.
     */
    private void playHeadToHead(Player player1,
                                Player player2,
                                int noOfRounds,
                                double noiseProbability,
                                Map<Player, RoundRobinResult> playerRecords)
    {
        HeadToHead game = new HeadToHead(player1, player2, noOfRounds, noiseProbability);
        game.run();
        int player1Aggregate = game.getHistory().getAggregatePayOffForPlayer(player1);
        int player2Aggregate = game.getHistory().getAggregatePayOffForPlayer(player2);
        RoundRobinResult player1Record = getPlayerRecord(playerRecords, player1);
        player1Record.addHeadToHeadResult(new HeadToHeadResult(player1,
                                                               player2,
                                                               player1Aggregate,
                                                               player2Aggregate,
                                                               noOfRounds));
        RoundRobinResult player2Record = getPlayerRecord(playerRecords, player2);
        player2Record.addHeadToHeadResult(new HeadToHeadResult(player2,
                                                               player1,
                                                               player2Aggregate,
                                                               player1Aggregate,
                                                               noOfRounds));
    }
    
    
    private RoundRobinResult getPlayerRecord(Map<Player, RoundRobinResult> records,
                                             Player player)
    {
        RoundRobinResult record = records.get(player);
        if (record == null)
        {
            record = new RoundRobinResult(player);
            records.put(player, record);
        }
        return record;
    }
}
