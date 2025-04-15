package edu.stanford.protege.webprotege.identity.ids;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IdHelperTest {

    @Test
    public void GIVEN_validSeed_WHEN_hashSeedCalled_THEN_returns64HexDigits() {
        long seed = 1;

        String hash = IdHelper.hashSeed(seed);

        assertNotNull(hash, "Hash should not be null");
        assertEquals(64, hash.length(), "SHA-256 hash should be 64 hex digits long");
        assertTrue(hash.matches("[0-9a-f]{64}"), "Hash should contain only lowercase hexadecimal characters");
    }

    @Test
    public void GIVEN_sameSeed_WHEN_hashSeedCalledMultipleTimes_THEN_returnsConsistentResult() {
        long seed = 12345;

        String hash1 = IdHelper.hashSeed(seed);
        String hash2 = IdHelper.hashSeed(seed);

        assertEquals(hash1, hash2, "Hash function should be deterministic for the same seed value");
    }

    @Test
    public void GIVEN_differentSeeds_WHEN_hashSeedCalled_THEN_returnsDifferentResults() {
        String hash1 = IdHelper.hashSeed(1);
        String hash2 = IdHelper.hashSeed(2);

        assertNotEquals(hash1, hash2, "Hash function should produce different outputs for different seed values");
    }

    @Test
    public void GIVEN_validHash_WHEN_extractNineDigitNumberInStringFromHashCalled_THEN_returns9DigitString() {
        String hash = IdHelper.hashSeed(1);

        String nineDigit = IdHelper.extractNineDigitNumberInStringFromHash(hash);

        assertNotNull(nineDigit, "Result should not be null");
        assertEquals(9, nineDigit.length(), "The numeric part should be 9 digits long");
        assertTrue(nineDigit.matches("\\d{9}"), "The numeric part should consist only of digits");
    }

    @Test
    public void GIVEN_invalidHash_WHEN_extractNineDigitNumberInStringFromHashCalled_THEN_throwsException() {
        String shortHash = "abc";

        assertThrows(IndexOutOfBoundsException.class, () ->
                        IdHelper.extractNineDigitNumberInStringFromHash(shortHash),
                "Should throw an exception if the hash is too short");
    }
}
