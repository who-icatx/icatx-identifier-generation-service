package edu.stanford.protege.webprotege.identity.ids;

import java.util.Random;

public class IdHelper {

    /**
     * Generates a 9-digit random number using the provided seed.
     * This method ensures the number is always exactly 9 digits without leading zeros.
     * 
     * @param seedValue the seed value for reproducible random generation
     * @return a 9-digit number as a string (between 100000000 and 999999999)
     */
    public static String generateNineDigitNumberFromSeed(long seedValue) {
        Random random = new Random(seedValue);
        // Generate a number between 100000000 and 999999999 (9 digits without leading zeros)
        long number = 100_000_000L + (Math.abs(random.nextLong()) % 900_000_000L);
        return String.valueOf(number);
    }
}
