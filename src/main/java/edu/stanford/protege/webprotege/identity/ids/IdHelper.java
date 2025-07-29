package edu.stanford.protege.webprotege.identity.ids;

import java.util.Random;

public class IdHelper {

    /**
     * Generates a 9-digit random number using the provided seed.
     * The number will be between 100000000 and 999999999 (inclusive).
     * 
     * @param seedValue the seed value for reproducible random generation
     * @return a 9-digit number as a string, padded with leading zeros if necessary
     */
    public static String generateNineDigitNumber(long seedValue) {
        Random random = new Random(seedValue);
        // Generate a number between 100000000 and 999999999 (9 digits)
        int nineDigitNumber = 100000000 + random.nextInt(900000000);
        return String.format("%09d", nineDigitNumber);
    }

    /**
     * Generates a 9-digit random number using the provided seed.
     * This method ensures the number is always exactly 9 digits.
     * 
     * @param seedValue the seed value for reproducible random generation
     * @return a 9-digit number as a string
     */
    public static String generateNineDigitNumberFromSeed(long seedValue) {
        Random random = new Random(seedValue);
        // Generate a number between 0 and 999999999, then ensure it's 9 digits
        long number = Math.abs(random.nextLong()) % 1_000_000_000L;
        return String.format("%09d", number);
    }

    // Keep the old methods for backward compatibility if needed
    @Deprecated
    public static String hashSeed(long seedValue) {
        // This method is kept for backward compatibility but should not be used
        throw new UnsupportedOperationException("hashSeed is deprecated. Use generateNineDigitNumber instead.");
    }

    @Deprecated
    public static String extractNineDigitNumberInStringFromHash(String hash) {
        // This method is kept for backward compatibility but should not be used
        throw new UnsupportedOperationException("extractNineDigitNumberInStringFromHash is deprecated. Use generateNineDigitNumber instead.");
    }
}
