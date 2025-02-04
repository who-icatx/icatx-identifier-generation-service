package edu.stanford.protege.webprotege.identity.ids;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class IdHelper {

    static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    public static String hashSeed(long seedValue) {
        byte[] hashBytes = messageDigest.digest(String.valueOf(seedValue).getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();

    }

    public static String extractNineDigitNumberInStringFromHash(String hash) {
        return String.format("%09d", Long.parseLong(hash.substring(0, 9), 16) % 1_000_000_000L);
    }

}
