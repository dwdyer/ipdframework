package au.edu.uwa.csse.dyerd01.ipd.util;

import au.edu.uwa.csse.dyerd01.ipd.framework.evolution.EvolutionResult;
import java.io.*;

/**
 * Utility for analysing the data in a data file.
 * @author Daniel Dyer
 */
public class DataAnalyser
{
    public static void main(String[] args)
    {
        EvolutionResult[] results = EvolutionResult.loadResults(new File(args[0]));
        // Overall stats.        
        double aggregateAverage = 0;
        double aggregateFitness = 0;
        double aggregateAgainstFixed = 0;
        double aggregateFixedPlayerAverage = 0;
        double aggregateFitnessRatio = 0;
            
        // Individual counts.
        int[] counts = new int[32];
        // Always Cooperate and Always Defect (including behaviourly equivalent variants).
        int advCount = 0;
        int acvCount = 0;
            
        // Add up totals.
        for (int i = 0; i < results.length; i++)
        {
            aggregateAverage += results[i].getAveragePayOff();
            aggregateFitness += results[i].getFitness();
            aggregateAgainstFixed += results[i].getAverageAgainstFixedPlayer();
            aggregateFixedPlayerAverage += results[i].getFixedPlayerAverage();
            aggregateFitnessRatio += results[i].getAverageRatio();
                
            int[] classifications = results[i].getClassifications();
            for (int j = 0; j < counts.length; j++)
            {
                counts[j] += classifications[j];
            }
            advCount += classifications[0] + classifications[4] + classifications[8] + classifications[12];
            acvCount += classifications[28] + classifications[29] + classifications[30] + classifications[31];
        }
        // Calculate means.
        double averageAverage = aggregateAverage / results.length;
        double fitnessAverage = aggregateFitness / results.length;
        double averageAgainstFixed = aggregateAgainstFixed / results.length;
        double fixedPlayerAverage = aggregateFixedPlayerAverage / results.length;
        double ratioAverage = aggregateFitnessRatio / results.length;

        double[] averages = new double[counts.length];
        for (int i = 0; i < counts.length; i++)
        {
            averages[i] = ((double) counts[i]) / results.length;
        }
        double advAverage = ((double) advCount) / results.length;
        double acvAverage = ((double) acvCount) / results.length;
            
        double averageDiffs = 0;
        double fitnessDiffs = 0;
        double againstFixedDiffs = 0;
        double fixedDiffs = 0;
        double ratioDiffs = 0;

        int[] diffs = new int[counts.length];
        int advDiffs = 0;
        int acvDiffs = 0;
        for (int i = 0; i < results.length; i++)
        {
            averageDiffs += Math.abs(averageAverage - results[i].getAveragePayOff());
            fitnessDiffs += Math.abs(fitnessAverage - results[i].getFitness());
            againstFixedDiffs += Math.abs(averageAgainstFixed - results[i].getAverageAgainstFixedPlayer());
            fixedDiffs += Math.abs(fixedPlayerAverage - results[i].getFixedPlayerAverage());
            ratioDiffs += Math.abs(ratioAverage - results[i].getAverageRatio());
                
            int[] classifications = results[i].getClassifications();
            // Calculate diffs.
            for (int j = 0; j < classifications.length; j++)
            {
                diffs[j] += Math.abs(averages[j] - classifications[j]);
            }
            advDiffs += Math.abs(advAverage - (classifications[0] + classifications[4] + classifications[8] + classifications[12]));
            acvDiffs += Math.abs(acvAverage - (classifications[28] + classifications[29] + classifications[30] + classifications[31]));
        }
        // Calculate standard deviations.
        double averageSD = averageDiffs / results.length;
        double fitnessSD = fitnessDiffs / results.length;
        double againstFixedSD = againstFixedDiffs / results.length;
        double fixedSD = fixedDiffs / results.length;
        double ratioSD = ratioDiffs / results.length;
            
        double[] standardDeviations = new double[counts.length];
        for (int i = 0; i < diffs.length; i++)
        {
            standardDeviations[i] = ((double) diffs[i]) / results.length;
        }        
        double advSD = ((double) advDiffs) / results.length;
        double acvSD = ((double) acvDiffs) / results.length;
            
        // Display results.
        System.out.println("Population Average: " + averageAverage + " (+/- " + averageSD + ")");
        System.out.println("Fitness Average: " + fitnessAverage + " (+/- " + fitnessSD + ")");
        System.out.println("Average vs. Fixed Player: " + averageAgainstFixed + " (+/- " + againstFixedSD + ")");
        System.out.println("Fixed Player Average: " + fixedPlayerAverage + " (+/- " + fixedSD + ")");
        System.out.println("Average Fitness Ratio: " + ratioAverage + " (+/- " + ratioSD + ")");
        System.out.println();
        System.out.println("(00)Always Defect:\t\t" + averages[0] + "\t(+/- " + standardDeviations[0] + ")");
        System.out.println("(01):\t\t\t\t" + averages[1] + "\t(+/- " + standardDeviations[1] + ")");
        System.out.println("(02):\t\t\t\t" + averages[2] + "\t(+/- " + standardDeviations[2] + ")");
        System.out.println("(03):\t\t\t\t" + averages[3] + "\t(+/- " + standardDeviations[3] + ")");
        System.out.println("(04):\t\t\t\t" + averages[4] + "\t(+/- " + standardDeviations[4] + ")");
        System.out.println("(05):\t\t\t\t" + averages[5] + "\t(+/- " + standardDeviations[5] + ")");
        System.out.println("(06):\t\t\t\t" + averages[6] + "\t(+/- " + standardDeviations[6] + ")");
        System.out.println("(07):\t\t\t\t" + averages[7] + "\t(+/- " + standardDeviations[7] + ")");
        System.out.println("(08):\t\t\t\t" + averages[8] + "\t(+/- " + standardDeviations[8] + ")");
        System.out.println("(09):Sus. Pavlov\t\t" + averages[9] + "\t(+/- " + standardDeviations[9] + ")");
        System.out.println("(10):Sus. Tit-For-Tat\t\t" + averages[10] + "\t(+/- " + standardDeviations[10] + ")");
        System.out.println("(11):\t\t\t\t" + averages[11] + "\t(+/- " + standardDeviations[11] + ")");
        System.out.println("(12):\t\t\t\t" + averages[12] + "\t(+/- " + standardDeviations[12] + ")");
        System.out.println("(13):\t\t\t\t" + averages[13] + "\t(+/- " + standardDeviations[13] + ")");
        System.out.println("(14):\t\t\t\t" + averages[14] + "\t(+/- " + standardDeviations[14] + ")");
        System.out.println("(15):\t\t\t\t" + averages[15] + "\t(+/- " + standardDeviations[15] + ")");
        System.out.println("(16):\t\t\t\t" + averages[16] + "\t(+/- " + standardDeviations[16] + ")");
        System.out.println("(17):\t\t\t\t" + averages[17] + "\t(+/- " + standardDeviations[17] + ")");
        System.out.println("(18):\t\t\t\t" + averages[18] + "\t(+/- " + standardDeviations[18] + ")");
        System.out.println("(19):\t\t\t\t" + averages[19] + "\t(+/- " + standardDeviations[19] + ")");
        System.out.println("(20):\t\t\t\t" + averages[20] + "\t(+/- " + standardDeviations[20] + ")");
        System.out.println("(21):\t\t\t\t" + averages[21] + "\t(+/- " + standardDeviations[21] + ")");
        System.out.println("(22):\t\t\t\t" + averages[22] + "\t(+/- " + standardDeviations[22] + ")");
        System.out.println("(23):\t\t\t\t" + averages[23] + "\t(+/- " + standardDeviations[23] + ")");
        System.out.println("(24):Grim\t\t\t" + averages[24] + "\t(+/- " + standardDeviations[24] + ")");
        System.out.println("(25):Pavlov\t\t\t" + averages[25] + "\t(+/- " + standardDeviations[25] + ")");
        System.out.println("(26):Tit-For-Tat\t\t" + averages[26] + "\t(+/- " + standardDeviations[26] + ")");
        System.out.println("(27):\t\t\t\t" + averages[27] + "\t(+/- " + standardDeviations[27] + ")");
        System.out.println("(28):\t\t\t\t" + averages[28] + "\t(+/- " + standardDeviations[28] + ")");
        System.out.println("(29):\t\t\t\t" + averages[29] + "\t(+/- " + standardDeviations[29] + ")");
        System.out.println("(30):\t\t\t\t" + averages[30] + "\t(+/- " + standardDeviations[30] + ")");
        System.out.println("(31):Always Cooperate\t\t" + averages[31] + "\t(+/- " + standardDeviations[31] + ")");
        System.out.println();
        System.out.println("Always Defect (inc. variants): " + advAverage + " (+/- " + advSD + ")");
        System.out.println("Always Cooperate (inc. variants): " + acvAverage + " (+/- " + acvSD + ")");
    }
}
