// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework.evolution;

import au.edu.uwa.csse.dyerd01.ipd.framework.*;
import au.edu.uwa.csse.dyerd01.ipd.strategies.*;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class Evolver
{
    private static final Logger logger = Logger.getLogger(Evolver.class);
    private static final RandomNumberGenerator RANDOM = RandomNumberGenerator.getInstance();
    
    private final EventListenerList listeners = new EventListenerList();
    
    private final boolean pureStrategies;
    private final int historyLength;
    private final int noOfGenerations;
    private final int populationSize;
    private final double mutationProbability;
    private final double standardDeviation;
    private final int noOfRounds;
    private final double noiseProbability;
    private final boolean playSelf;
    private final Player fixedOpponent;
    private final boolean coEvolvedFitnessFactor;
    private final int fixedPlayerFitnessFactor;
    
    private final ResultsComparator resultsComparator = new ResultsComparator();
    
    
    /**
     * Create an evolver with a single, fixed fitness ratio for all players.
     */
    public Evolver(boolean pureStrategies,
                   int historyLength,
                   int noOfGenerations,
                   int populationSize,
                   double mutationProbability,
                   double standardDeviation,
                   int noOfRounds,
                   double noiseProbability,
                   boolean playSelf,
                   Player fixedOpponent,
                   int fixedPlayerFitnessFactor) // 0% - 100%
    {
        this.pureStrategies = pureStrategies;
        this.historyLength = historyLength;        
        this.noOfGenerations = noOfGenerations;
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.standardDeviation = standardDeviation;
        this.noOfRounds = noOfRounds;
        this.noiseProbability = noiseProbability;
        this.playSelf = playSelf;
        this.fixedOpponent = fixedOpponent;
        if (fixedPlayerFitnessFactor < 0 || fixedPlayerFitnessFactor > 100)
        {
            throw new IllegalArgumentException("Invalid fitness factor.");
        }
        this.coEvolvedFitnessFactor = false;
        this.fixedPlayerFitnessFactor = fixedPlayerFitnessFactor;
        logger.info("Created evolver with fixed fitness ratio of " + fixedPlayerFitnessFactor + "%");
    }
    
    
    /**
     * Create an evolver that co-evolves fitness ratios for players.
     */
    public Evolver(boolean pureStrategies,
                   int historyLength,
                   int noOfGenerations,
                   int populationSize,
                   double mutationProbability,
                   double standardDeviation,
                   int noOfRounds,
                   double noiseProbability,
                   boolean playSelf,
                   Player fixedOpponent)
    {
        this.pureStrategies = pureStrategies;
        this.historyLength = historyLength;        
        this.noOfGenerations = noOfGenerations;
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.standardDeviation = standardDeviation;
        this.noOfRounds = noOfRounds;
        this.noiseProbability = noiseProbability;
        this.playSelf = playSelf;
        this.fixedOpponent = fixedOpponent;
        this.coEvolvedFitnessFactor = true;
        this.fixedPlayerFitnessFactor = -1;
        logger.info("Created evolver with co-evolved fitness ratios.");
    }
    
    
    public EvolutionResult[] start()
    {
        EvolutionResult[] results = new EvolutionResult[noOfGenerations];
        EvolvedPlayer[] population = null;
        ResultsWrapper[] roundRobinResults = null;
        for (int i = 0; i < noOfGenerations; i++)
        {
            if (i == 0)
            {
                logger.debug("Generating initial population of " + populationSize + " candidates...");
                population = generateInitialPopulation(populationSize);
            }
            else
            {
                assert roundRobinResults != null;
                logger.debug("Producing offspring generation...");
                population = generateOffspringGeneration(population, roundRobinResults);
            }
            
            RoundRobinResult fixedPlayerResult = null;
            int fixedPlayerRank = 1;
            if (fixedOpponent != null)
            {
                logger.debug("Playing generation against fixed opponent:");
                fixedPlayerResult = TournamentManager.getInstance().executeFixedOpponentAgainstEvolvedPopulation(fixedOpponent,
                                                                                                                 population,
                                                                                                                 noOfRounds,
                                                                                                                 noiseProbability);
            }

            logger.debug("Evaluating generation no. " + i + "...");
            roundRobinResults = evaluatePopulation(population, fixedPlayerResult);
            
            int aggregatePayOff = 0;
            int aggregateIterations = 0;
            double best = 0;
            double aggregateScoreAgainstFixedPlayer = 0;
            double aggregateFitness = 0;
            double maxRatio = 0;
            double minRatio = 1;
            double aggregateRatio = 0;
            for (int j = 0; j < roundRobinResults.length; j++)
            {
                double playerAverage = roundRobinResults[j].getResult().getAveragePayOff();
                aggregatePayOff += roundRobinResults[j].getResult().getAggregatePayOff();
                aggregateIterations += roundRobinResults[j].getResult().getIterations();
                aggregateScoreAgainstFixedPlayer += roundRobinResults[j].getScoreAgainstFixedOpponent();
                aggregateFitness += roundRobinResults[j].getFitness();
                best = Math.max(best, playerAverage);
                if (fixedPlayerResult != null && playerAverage > fixedPlayerResult.getAveragePayOff())
                {
                    fixedPlayerRank++;
                }
                
                // Calculate figures relating to co-evolved fitness ratio.
                if (coEvolvedFitnessFactor)
                {
                    double playerRatio = ((EvolvedPlayer) roundRobinResults[j].getResult().getPlayer()).getFitnessRatio();
                    maxRatio = Math.max(maxRatio, playerRatio);
                    minRatio = Math.min(minRatio, playerRatio);
                    aggregateRatio += playerRatio;
                }
            }
            double average = ((double) aggregatePayOff) / aggregateIterations;
            double againstFixed = aggregateScoreAgainstFixedPlayer / roundRobinResults.length;
            double fitness = aggregateFitness / roundRobinResults.length;
            double averageRatio = aggregateRatio / roundRobinResults.length;
            
            if (fixedPlayerResult == null)
            {
                results[i] = new EvolutionResult(population[0],
                                                 best,
                                                 average,
                                                 againstFixed,
                                                 classifyPopulation(population));
            }
            else if (coEvolvedFitnessFactor)
            {
                // Calculate ratio standard deviation.
                double diffs = 0;
                for (int j = 0; j < roundRobinResults.length; j++)
                {
                    diffs += Math.abs(averageRatio - ((EvolvedPlayer) roundRobinResults[j].getResult().getPlayer()).getFitnessRatio());
                }
                double ratioStandardDeviation = diffs / roundRobinResults.length;
                results[i] = new EvolutionResult(population[0],
                                                 best,
                                                 average,
                                                 againstFixed,
                                                 classifyPopulation(population),
                                                 fixedPlayerResult.getAveragePayOff(),
                                                 fixedPlayerRank,
                                                 fitness,
                                                 maxRatio,
                                                 minRatio,
                                                 averageRatio,
                                                 ratioStandardDeviation);
            }
            else
            {
                results[i] = new EvolutionResult(population[0],
                                                 best,
                                                 average,
                                                 againstFixed,
                                                 classifyPopulation(population),
                                                 fixedPlayerResult.getAveragePayOff(),
                                                 fixedPlayerRank,
                                                 fitness);
            }
            logger.info(i + " - " + results[i].toString());
            
            fireNotifyProgress();
        }
        return results;
    }

    
    private EvolvedPlayer[] generateInitialPopulation(int size)
    {
        EvolvedPlayer[] initialPopulation = new EvolvedPlayer[size];
        logger.debug("Initial population: ");
        for (int i = 0; i < initialPopulation.length; i++)
        {
            if (coEvolvedFitnessFactor)
            {
                initialPopulation[i] = EvolvedPlayer.generateRandomPlayer(historyLength, pureStrategies);
            }
            else
            {
                initialPopulation[i] = EvolvedPlayer.generateRandomPlayer(historyLength,
                                                                          pureStrategies,
                                                                          (fixedPlayerFitnessFactor / 100)); // Convert from percentage to decimal.
            }
            logger.debug("  " + initialPopulation[i]);
        }
        return initialPopulation;
    }
    
    
    private ResultsWrapper[] evaluatePopulation(EvolvedPlayer[] population, RoundRobinResult fixedOpponentResults)
    {
        RoundRobinResult[] results = TournamentManager.getInstance().executeRoundRobinTournament(population,
                                                                                                 noOfRounds,
                                                                                                 noiseProbability,
                                                                                                 playSelf);
        ResultsWrapper[] wrappedResults = new ResultsWrapper[results.length];
        for (int i = 0; i < results.length; i++)
        {
            if (fixedOpponentResults == null)
            {
                wrappedResults[i] = new ResultsWrapper(results[i]);
            }
            else
            {
                wrappedResults[i] = new ResultsWrapper(results[i], fixedOpponentResults.getHeadToHeadResult(results[i].getPlayer()).getAverageOpponentPayOff());
            }
        }
        Arrays.sort(wrappedResults, resultsComparator);
        // logger.info("Best: " + results[0].getPlayer().getName() + " scored " + results[0].getAggregatePayOff());
        return wrappedResults;
    }
    
        
    private EvolvedPlayer[] generateOffspringGeneration(EvolvedPlayer[] parentGeneration, ResultsWrapper[] results)
    {
        // Record fitness values for fitness-proportionate selection.
        double[] fitnessValues = new double[parentGeneration.length];
        EvolvedPlayer[] oldGeneration = new EvolvedPlayer[parentGeneration.length];
        double aggregateFitness = 0;
        for (int i = 0; i < results.length; i++)
        {
            oldGeneration[i] = (EvolvedPlayer) results[i].getResult().getPlayer();
            aggregateFitness += results[i].getFitness();
            // Each value in the fitnessValues array is cumulative it is the total
            // of the current value plus all of the previous values.
            fitnessValues[i] = aggregateFitness;
        }

        // Mutation
        EvolvedPlayer[] offspringGeneration = new EvolvedPlayer[parentGeneration.length];
        for (int i = 0; i < offspringGeneration.length; i++)
        {
            // Uses fitness proportionate selection.
            double selectionValue = RANDOM.nextDouble() * aggregateFitness;
            EvolvedPlayer selectedParent = null;
            // Loop through the cumulative fitness values until we reach the interval
            // that the random number fits into.
            for (int j = 0; j < fitnessValues.length; j++)
            {
                if (selectionValue <= fitnessValues[j])
                {
                    selectedParent = oldGeneration[j];
                    break;
                }
            }
            offspringGeneration[i] = selectedParent.mutate(mutationProbability, standardDeviation, pureStrategies);
        }
        return offspringGeneration;
    }
    
    
    private int[] classifyPopulation(EvolvedPlayer[] population)
    {
        int[] classifications = new int[32]; // Hard-coded for history length of 1.
        for (int i = 0; i < population.length; i++)
        {
            classifications[population[i].classify()]++;
        }
        return classifications;
    }
    
    
    public void addEvolutionListener(EvolutionListener listener)
    {
        listeners.add(EvolutionListener.class, listener);
    }
    
    
    public void removeEvolutionListener(EvolutionListener listener)
    {
        listeners.remove(EvolutionListener.class, listener);
    }
    
    
    protected void fireNotifyProgress()
    {
        EventListener[] interested = listeners.getListeners(EvolutionListener.class);
        for (int i = 0; i < interested.length; i++)
        {
            ((EvolutionListener) interested[i]).notifyGenerationProcessed();
        }
    }
    
    
    private class ResultsWrapper
    {
        private final RoundRobinResult result;
        private final double scoreAgainstFixedOpponent;
        private final double fitness;
        
        public ResultsWrapper(RoundRobinResult result)
        {
            this.result = result;
            this.scoreAgainstFixedOpponent = 0;
            this.fitness = result.getAveragePayOff();
        }
        
        public ResultsWrapper(RoundRobinResult result, double scoreAgainstFixedOpponent)
        {
            EvolvedPlayer player = (EvolvedPlayer) result.getPlayer();
            this.result = result;
            this.scoreAgainstFixedOpponent = scoreAgainstFixedOpponent;
            this.fitness = (scoreAgainstFixedOpponent * player.getFitnessRatio()
                            + result.getAveragePayOff() * (1 - player.getFitnessRatio()));
        }
        
        public RoundRobinResult getResult()
        {
            return result;
        }
        
        public double getScoreAgainstFixedOpponent()
        {
            return scoreAgainstFixedOpponent;
        }
        
        public double getFitness()
        {
            return fitness;
        }
    }
    
    private static class ResultsComparator implements Comparator
    {
        public int compare(Object obj1, Object obj2)
        {
            ResultsWrapper result1 = (ResultsWrapper) obj1;
            ResultsWrapper result2 = (ResultsWrapper) obj2;
            return (int) (result1.getFitness() - result2.getFitness()) * 100000;
        }
    }
    
    
// ========== Static stuff below is for running in command-line mode. ==========
    
    // Defaults for all parameters.
    private static boolean param_pure;
    private static int param_generations = 1000;
    private static int param_populationSize = 100;
    private static double param_mutationProbability = 0.1;
    private static double param_standardDeviation = 0.1;
    private static int param_rounds = 1000;
    private static double param_noiseProbability;
    private static boolean param_playSelf = false;
    private static int param_fixedPlayerFitnessFactor = 0;
    private static boolean param_coEvolvedFitnessFactor = true;
    private static Player param_fixedPlayer = new ApproximatingOpponentModeller();
    // private static Player param_fixedPlayer = null;
    
    
    public static void main(String[] args)
    {
        readArguments(args);
        checkArguments();        
        try
        {
            System.getProperties().load(new FileInputStream("log4j.properties"));
            org.apache.log4j.PropertyConfigurator.configure(System.getProperties());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.err.println("Could not load log4j properties.");
            System.exit(-1);
        }
            
        EvolutionResult[] results = null;
        if (param_coEvolvedFitnessFactor)
        {
            results = TournamentManager.getInstance().executeEvolution(param_pure,
                                                                       param_generations,
                                                                       param_populationSize,
                                                                       param_mutationProbability,
                                                                       param_standardDeviation,
                                                                       param_rounds,
                                                                       param_noiseProbability,
                                                                       param_playSelf,
                                                                       param_fixedPlayer,
                                                                       null);            
        }
        else
        {
            results = TournamentManager.getInstance().executeEvolution(param_pure,
                                                                       param_generations,
                                                                       param_populationSize,
                                                                       param_mutationProbability,
                                                                       param_standardDeviation,
                                                                       param_rounds,
                                                                       param_noiseProbability,
                                                                       param_playSelf,
                                                                       param_fixedPlayer,
                                                                       param_fixedPlayerFitnessFactor,
                                                                       null);
        }
        saveResults(results);
    }
    
    
    private static void saveResults(EvolutionResult[] results)
    {
        // Build up file name from parameters.
        StringBuffer fileName = new StringBuffer();
        fileName.append(param_pure ? "pure" : "impure");
        fileName.append('_');
        fileName.append(param_populationSize);
        fileName.append('_');
        fileName.append(param_generations);
        fileName.append('_');
        fileName.append(param_mutationProbability);
        if (!param_pure)
        {
            fileName.append('_');
            fileName.append(param_standardDeviation);
        }
        fileName.append('_');
        fileName.append(param_fixedPlayerFitnessFactor);
        fileName.append("%.tsv");
        File file = new File(fileName.toString());
        EvolutionResult.saveResults(results, file);
    }
    
    
    /**
     * Process the command-line arguments and check their validity.
     */
    private static void readArguments(String[] args)
    {
        try
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-pure"))
                {
                    param_pure = true;
                }
                else if (args[i].equalsIgnoreCase("-impure") || args[i].equalsIgnoreCase("-mixed"))
                {
                    param_pure = false;
                }
                else if (args[i].equalsIgnoreCase("-grim"))
                {
                    param_fixedPlayer = new Grim();
                }
                else if (args[i].equalsIgnoreCase("-pavlov"))
                {
                    param_fixedPlayer = new Pavlov();
                }
                else if (args[i].equalsIgnoreCase("-tft"))
                {
                    param_fixedPlayer = new TitForTat();
                }
                else if (args[i].equalsIgnoreCase("-gradual"))
                {
                    param_fixedPlayer = new Gradual();
                }
                else if (args[i].equalsIgnoreCase("-evolutiononly"))
                {
                    param_fixedPlayer = null;
                    param_coEvolvedFitnessFactor = false;
                }
                else if (args[i].equalsIgnoreCase("-playself"))
                {
                    param_playSelf = true;
                }
                else if (args[i].equalsIgnoreCase("-generations") || args[i].equalsIgnoreCase("-gen"))
                {
                    param_generations = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-populationsize") || args[i].equalsIgnoreCase("-pop"))
                {
                    param_populationSize = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-rounds"))
                {
                    param_rounds = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-mutationprobability") || args[i].equalsIgnoreCase("-mp"))
                {
                    param_mutationProbability = Double.parseDouble(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-standarddeviation") || args[i].equalsIgnoreCase("-sd"))
                {
                    param_standardDeviation = Double.parseDouble(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-noiseprobability") || args[i].equalsIgnoreCase("-np"))
                {
                    param_noiseProbability = Double.parseDouble(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-fixedfitnessfactor") || args[i].equalsIgnoreCase("-fitness"))
                {
                    param_fixedPlayerFitnessFactor = Integer.parseInt(args[++i]);
                    param_coEvolvedFitnessFactor = false;
                }            
                else
                {
                    System.err.println("Invalid argument.");
                    displayUsage();
                }
            }
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Invalid numeric value.");
            displayUsage();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            System.err.println("Invalid arguments.");
            displayUsage();
        }       
    }
    
    
    private static void checkArguments()
    {
        boolean ok = true;
        if (param_noiseProbability < 0 || param_noiseProbability > 1)
        {
            System.err.println("Noise probability must be in the range 0-1.");
            ok = false;
        }
        if (param_mutationProbability < 0 || param_mutationProbability > 1)
        {
            System.err.println("Mutation probability must be in the range 0-1.");
            ok = false;
        }
        if (param_standardDeviation <= 0)
        {
            System.err.println("Standard deviation must be greater than 0.");
            ok = false;
        }
        if (param_fixedPlayerFitnessFactor < 0 || param_fixedPlayerFitnessFactor > 100)
        {
            System.err.println("Fixed player fitness factor (%) must be in the range 0-100.");
            ok = false;
        }
        if (!ok)
        {
            System.exit(-1);
        }
    }
    
    
    private static void displayUsage()
    {
        System.err.println("<java command> [-pure | -impure]");        
        System.err.println("               [-generations n]");
        System.err.println("               [-populationsize n]");
        System.err.println("               [-mutationprobability n]");
        System.err.println("               [-standarddeviation n]");
        System.err.println("               [-evolutiononly]");
        System.err.println("               [-rounds n]");
        System.err.println("               [-noiseprobability n]");
        System.err.println("               [-playself]");
        System.err.println("               [-fixedfitnessfactor n]");
        System.exit(-1);
    }
}
