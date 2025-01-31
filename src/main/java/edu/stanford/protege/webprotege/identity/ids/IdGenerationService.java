package edu.stanford.protege.webprotege.identity.ids;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdGenerationService {

    private final SeedRepository seedRepository;

    private final IdentificationRepository identificationRepository;

    private static final String SEED_NAME = "id_seed";
    private volatile long lastSeedValue = -1;
    private final Set<String> existingIdsCache = ConcurrentHashMap.newKeySet();

    public IdGenerationService(SeedRepository seedRepository,
                               IdentificationRepository identificationRepository) {
        this.seedRepository = seedRepository;
        this.identificationRepository = identificationRepository;
    }

    public String generateUniqueId(String prefix) {
        if (existingIdsCache.isEmpty()) {
            existingIdsCache.addAll(identificationRepository.getExistingIds());
        }

        long seedValue = getNextSeedValue();
        String uniqueId;

        do {
            uniqueId = String.format("%s%09d", prefix, extractNineDigitNumber(hashSeed(seedValue)));
            seedValue++;
        } while (existingIdsCache.contains(uniqueId));

        existingIdsCache.add(uniqueId);
        identificationRepository.saveListInPages(List.of(uniqueId));

        updateSeedValue(seedValue);
        return uniqueId;
    }


    private synchronized long getNextSeedValue() {
        if (lastSeedValue == -1) {
            lastSeedValue = seedRepository.findById(SEED_NAME).orElse(new Seed(SEED_NAME, 0)).getValue();
        }
        return lastSeedValue + 1;
    }

    private void updateSeedValue(long seedValue) {
        lastSeedValue = seedValue;
        seedRepository.save(new Seed(SEED_NAME, seedValue));
    }

    private String hashSeed(long seedValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(String.valueOf(seedValue).getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private long extractNineDigitNumber(String hash) {
        return (Long.parseLong(hash.substring(0, 9), 16) % 1_000_000_000L);
    }
}
