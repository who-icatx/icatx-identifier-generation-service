package edu.stanford.protege.webprotege.identity.ids;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IdHelperTest {

    @Test
    public void GIVEN_validSeed_WHEN_generateNineDigitNumberCalled_THEN_returns9DigitString() {
        long seed = 1;

        String result = IdHelper.generateNineDigitNumber(seed);

        assertNotNull(result, "Result should not be null");
        assertEquals(9, result.length(), "Result should be 9 digits long");
        assertTrue(result.matches("\\d{9}"), "Result should consist only of digits");
        // The number should be between 100000000 and 999999999 (9 digits starting with 1-9)
        assertTrue(result.matches("[1-9]\\d{8}"), "Result should be a 9-digit number starting with 1-9");
    }

    @Test
    public void GIVEN_sameSeed_WHEN_generateNineDigitNumberCalledMultipleTimes_THEN_returnsConsistentResult() {
        long seed = 12345;

        String result1 = IdHelper.generateNineDigitNumber(seed);
        String result2 = IdHelper.generateNineDigitNumber(seed);

        assertEquals(result1, result2, "Random generator should be deterministic for the same seed value");
    }

    @Test
    public void GIVEN_differentSeeds_WHEN_generateNineDigitNumberCalled_THEN_returnsDifferentResults() {
        String result1 = IdHelper.generateNineDigitNumber(1);
        String result2 = IdHelper.generateNineDigitNumber(2);

        assertNotEquals(result1, result2, "Random generator should produce different outputs for different seed values");
    }

    @Test
    public void GIVEN_validSeed_WHEN_generateNineDigitNumberFromSeedCalled_THEN_returns9DigitString() {
        long seed = 1;

        String result = IdHelper.generateNineDigitNumberFromSeed(seed);

        assertNotNull(result, "Result should not be null");
        assertEquals(9, result.length(), "Result should be 9 digits long");
        assertTrue(result.matches("\\d{9}"), "Result should consist only of digits");
    }

    @Test
    public void GIVEN_sameSeed_WHEN_generateNineDigitNumberFromSeedCalledMultipleTimes_THEN_returnsConsistentResult() {
        long seed = 12345;

        String result1 = IdHelper.generateNineDigitNumberFromSeed(seed);
        String result2 = IdHelper.generateNineDigitNumberFromSeed(seed);

        assertEquals(result1, result2, "Random generator should be deterministic for the same seed value");
    }

    @Test
    public void GIVEN_differentSeeds_WHEN_generateNineDigitNumberFromSeedCalled_THEN_returnsDifferentResults() {
        String result1 = IdHelper.generateNineDigitNumberFromSeed(1);
        String result2 = IdHelper.generateNineDigitNumberFromSeed(2);

        assertNotEquals(result1, result2, "Random generator should produce different outputs for different seed values");
    }

    @Test
    public void GIVEN_deprecatedMethods_WHEN_called_THEN_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
                IdHelper.hashSeed(1), "Deprecated hashSeed method should throw UnsupportedOperationException");

        assertThrows(UnsupportedOperationException.class, () ->
                IdHelper.extractNineDigitNumberInStringFromHash("abc"), 
                "Deprecated extractNineDigitNumberInStringFromHash method should throw UnsupportedOperationException");
    }
}
