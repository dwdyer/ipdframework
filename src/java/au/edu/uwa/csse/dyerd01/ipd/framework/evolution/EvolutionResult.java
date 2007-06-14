// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework.evolution;

import au.edu.uwa.csse.dyerd01.ipd.framework.RandomNumberGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class EvolutionResult
{
    private static final Logger logger = Logger.getLogger(EvolutionResult.class);
    
    private final EvolvedPlayer bestCandidate;
    private final double bestCandidateAveragePayOff;
    private final double averagePayOff;
    private final double averageAgainstFixedPlayer; // Added October 2005.
    // Counts for each of the possible strategy categories.
    private final int[] classifications;
    
    // Fixed opponent data.
    private final double fixedPlayerAverage;
    private final int fixedPlayerRank;
    private final double fitness;
    
    // Fitness ratio data (for co-evolved fitness).
    private final boolean coEvo;
    private final double maxRatio;
    private final double minRatio;
    private final double averageRatio;
    private final double ratioStandardDeviation;
    
    public EvolutionResult(EvolvedPlayer bestCandidate,
                           double bestCandidateAveragePayOff,
                           double averagePayOff,
                           double averageAgainstFixedPlayer,
                           int[] classifications)
    {
        this(bestCandidate,
             bestCandidateAveragePayOff,
             averagePayOff,
             averageAgainstFixedPlayer, // Added October 2005.
             classifications,
             0.0,
             0,
             averagePayOff);
    }
    
    
    public EvolutionResult(EvolvedPlayer bestCandidate,
                           double bestCandidateAveragePayOff,
                           double averagePayOff,
                           double averageAgainstFixedPlayer, // Added October 2005.
                           int[] classifications,
                           double fixedPlayerAverage,
                           int fixedPlayerRank,
                           double fitness)
    {
        this.bestCandidate = bestCandidate;
        this.bestCandidateAveragePayOff = bestCandidateAveragePayOff;
        this.averagePayOff = averagePayOff;
        this.averageAgainstFixedPlayer = averageAgainstFixedPlayer; // Added October 2005.
        this.classifications = classifications;
        this.fixedPlayerAverage = fixedPlayerAverage;
        this.fixedPlayerRank = fixedPlayerRank;
        this.fitness = fitness;
        this.coEvo = false;
        this.maxRatio = -1;
        this.minRatio = -1;
        this.averageRatio = -1;
        this.ratioStandardDeviation = -1;
    }
    
    
    public EvolutionResult(EvolvedPlayer bestCandidate,
                           double bestCandidateAveragePayOff,
                           double averagePayOff,
                           double averageAgainstFixedPlayer, // Added October 2005.
                           int[] classifications,
                           double fixedPlayerAverage,
                           int fixedPlayerRank,
                           double fitness,
                           double maxRatio,
                           double minRatio,
                           double averageRatio,
                           double ratioStandardDeviation)
    {
        this.bestCandidate = bestCandidate;
        this.bestCandidateAveragePayOff = bestCandidateAveragePayOff;
        this.averagePayOff = averagePayOff;
        this.averageAgainstFixedPlayer = averageAgainstFixedPlayer; // Added October 2005.
        this.classifications = classifications;
        this.fixedPlayerAverage = fixedPlayerAverage;
        this.fixedPlayerRank = fixedPlayerRank;
        this.fitness = fitness;
        this.maxRatio = maxRatio;
        this.minRatio = minRatio;
        this.averageRatio = averageRatio;
        this.ratioStandardDeviation = ratioStandardDeviation;
        this.coEvo = true;
    }

    
    
    public double getBestPayOff()
    {
        return bestCandidateAveragePayOff;
    }
    
    public double getAveragePayOff()
    {
        return averagePayOff;
    }
    
    public double getAverageAgainstFixedPlayer()
    {
        return averageAgainstFixedPlayer;
    }
    
    
    public double getFixedPlayerAverage()
    {
        return fixedPlayerAverage;
    }
    
    
    public int getFixedPlayerRank()
    {
        return fixedPlayerRank;
    }
    
    
    public int[] getClassifications()
    {
        return classifications.clone();
    }
    
    
    public double getFitness()
    {
        return fitness;
    }
    
    
    public double getMaxRatio()
    {
        return maxRatio;
    }
    
    
    public double getMinRatio()
    {
        return minRatio;
    }
    

    public double getAverageRatio()
    {
        return averageRatio;
    }
    
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer("Av: ");
        buffer.append(averagePayOff);
        buffer.append(" Best: ");
        buffer.append(bestCandidateAveragePayOff);
        buffer.append(" FixedAv: ");
        buffer.append(fixedPlayerAverage);
        buffer.append(" FixedRank: ");
        buffer.append(fixedPlayerRank);
        buffer.append(" Fitness: ");
        buffer.append(fitness);
        for (int classification : classifications)
        {
            buffer.append(' ');
            buffer.append(classification);
        }
        if (coEvo)
        {
            buffer.append(" MaxRatio: ");
            buffer.append(maxRatio);
            buffer.append(" MinRatio: ");
            buffer.append(minRatio);
            buffer.append(" AvRatio: ");
            buffer.append(averageRatio);
            buffer.append(" RatioSD: ");
            buffer.append(ratioStandardDeviation);            
        }
        return buffer.toString();
    }
    
    
    public String toRecordString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(averagePayOff);
        buffer.append('\t');
        buffer.append(bestCandidateAveragePayOff);
        buffer.append('\t');
        buffer.append(averageAgainstFixedPlayer); // Added October 2005.
        buffer.append('\t'); // Added October 2005.
        buffer.append(fixedPlayerAverage);
        buffer.append('\t');
        buffer.append(fixedPlayerRank);
        buffer.append('\t');
        buffer.append(fitness);
        for (int classification : classifications)
        {
            buffer.append('\t');
            buffer.append(classification);
        }
        if (coEvo)
        {
            buffer.append('\t');
            buffer.append(maxRatio);
            buffer.append('\t');
            buffer.append(minRatio);
            buffer.append('\t');
            buffer.append(averageRatio);
            buffer.append('\t');
            buffer.append(ratioStandardDeviation);
        }
        return buffer.toString();
    }
    
    
    public static EvolutionResult[] loadResults(File file)
    {
        try
        {
            logger.debug("Loading results from file " + file.toString());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<EvolutionResult> results = new ArrayList<EvolutionResult>();
            String line = reader.readLine();
            boolean coEvo = line.trim().equals("#!COEVO");
            while (line != null)
            {
                if (line.trim().charAt(0) != '#') // Ignore comments.
                {
                    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                    tokenizer.nextToken(); // Skip over generation no.
                    double populationAv = Double.parseDouble(tokenizer.nextToken());
                    double populationBest = Double.parseDouble(tokenizer.nextToken());
                    // double averageAgainstFixed = 0; // Added October 2005.
                    double averageAgainstFixed = Double.parseDouble(tokenizer.nextToken()); // Added October 2005.
                    double fixedAv = Double.parseDouble(tokenizer.nextToken());
                    int fixedRank = Integer.parseInt(tokenizer.nextToken());
                    double fitness = Double.parseDouble(tokenizer.nextToken());
                    int[] classifications = new int[32]; // Assumes look-back of 1.
                    for (int i = 0; i < classifications.length; i++)
                    {
                        classifications[i] = Integer.parseInt(tokenizer.nextToken());
                    }
                    if (coEvo)
                    {
                        double maxRatio = Double.parseDouble(tokenizer.nextToken());
                        double minRatio = Double.parseDouble(tokenizer.nextToken());
                        double averageRatio = Double.parseDouble(tokenizer.nextToken());
                        double ratioStandardDeviation = Double.parseDouble(tokenizer.nextToken());
                        results.add(new EvolutionResult(null,
                                                        populationBest,
                                                        populationAv,
                                                        averageAgainstFixed, // Added October 2005.
                                                        classifications,
                                                        fixedAv,
                                                        fixedRank,
                                                        fitness,
                                                        maxRatio,
                                                        minRatio,
                                                        averageRatio,
                                                        ratioStandardDeviation));
                    }
                    else
                    {
                        results.add(new EvolutionResult(null,
                                                        populationBest,
                                                        populationAv,
                                                        averageAgainstFixed, // Added October 2005.
                                                        classifications,
                                                        fixedAv,
                                                        fixedRank,
                                                        fitness));
                    }
                }
                line = reader.readLine();
            }
            logger.debug("Finished loading " + results.size() + " records.");
            return results.toArray(new EvolutionResult[results.size()]);
        }
        catch (IOException ex)
        {
            logger.error("Error loading results file.", ex);
            return null;
        }
    }
    
    
    public static void saveResults(EvolutionResult[] results, File file)
    {
        PrintWriter writer = null;
        try
        {
            logger.debug("Saving results to file " + file.toString());
            writer = new PrintWriter(new FileWriter(file), true);
            if (results[0].coEvo)
            {
                writer.println("#!COEVO"); // Indicates file includes data on co-evolved fitness ratio.
            }
            writer.println("# Random seed: " + RandomNumberGenerator.getInstance().getSeed());
            writer.println("# Finished at: " + new Date().toString());
            for (int i = 0; i < results.length; i++)
            {
                writer.println(i + "\t" + results[i].toRecordString());
            }
        }
        catch (IOException ex)
        {
            logger.error("Error writing results file.", ex);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
}
