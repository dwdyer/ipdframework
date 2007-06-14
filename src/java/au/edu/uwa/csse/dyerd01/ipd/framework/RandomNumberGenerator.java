// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.framework;

import java.util.Random;
import org.apache.log4j.Logger;

/**
 * @author Daniel Dyer
 */
public class RandomNumberGenerator
{
    private static final Logger logger = Logger.getLogger(RandomNumberGenerator.class);
    
    private static final RandomNumberGenerator INSTANCE = new RandomNumberGenerator();
    
    private final Random random;
    private final long seed;
    
    /**
     * Private constructor prevents direct instantiation and enforces Singleton pattern.
     */
    private RandomNumberGenerator()
    {
        String property = System.getProperties().getProperty("random.seed");
        seed = property == null ? System.currentTimeMillis() : Long.parseLong(property);
        System.out.println("Seeded random number generator with seed " + seed);
        random = new Random(seed);
    }
    
    
    public static RandomNumberGenerator getInstance()
    {
        return INSTANCE;
    }
    
    
    /**
     * @return The value used to seed this random number generator.
     */
    public long getSeed()
    {
        return seed;
    }
    
    public double nextDouble()
    {
        return random.nextDouble();
    }

    
    public int nextInt(int n)
    {
        return random.nextInt(n);
    }
    
    
    public double nextGaussian(double mean, double standardDeviation)
    {
        return random.nextGaussian() * standardDeviation + mean;
    }
}