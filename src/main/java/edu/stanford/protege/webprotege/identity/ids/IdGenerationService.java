package edu.stanford.protege.webprotege.identity.ids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static edu.stanford.protege.webprotege.identity.ids.IdHelper.*;

@Service
public class IdGenerationService {
    private static final Logger logger = LoggerFactory.getLogger(IdGenerationService.class);
    private final SeedRepository seedRepository;
    private final IdentificationRepository identificationRepository;
    private static final String SEED_NAME = "id_seed";
    private volatile long lastSeedValue = -1;

    public IdGenerationService(SeedRepository seedRepository,
                             IdentificationRepository identificationRepository) {
        this.seedRepository = seedRepository;
        this.identificationRepository = identificationRepository;
    }

    @Transactional
    @Retryable(
        value = {OptimisticLockingFailureException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public String generateUniqueId(String prefix) {
        try {
            long seedValue = getSeedValue();
            String uniqueId;

            do {
                seedValue++;
                uniqueId = String.format("%s%s", prefix, extractNineDigitNumberInStringFromHash(hashSeed(seedValue)));
            } while (identificationRepository.exists(uniqueId));

            identificationRepository.saveListInPages(List.of(uniqueId));
            updateSeedValue(seedValue);
            return uniqueId;
        } catch (Exception e) {
            logger.error("Error generating unique ID for prefix: {}", prefix, e);
            throw e;
        }
    }

    private long getSeedValue() {
        if (lastSeedValue == -1) {
            try {
                Seed seed = seedRepository.findById(SEED_NAME)
                    .orElseGet(() -> {
                        Seed newSeed = new Seed(SEED_NAME, 0);
                        return seedRepository.save(newSeed);
                    });
                lastSeedValue = seed.getValue();
            } catch (Exception e) {
                logger.error("Error getting seed value", e);
                throw e;
            }
        }
        return lastSeedValue;
    }

    private void updateSeedValue(long seedValue) {
        try {
            lastSeedValue = seedValue;
            Seed seed = seedRepository.findById(SEED_NAME)
                .orElseThrow(() -> new IllegalStateException("Seed not found"));
            seed.setValue(seedValue);
            seedRepository.save(seed);
        } catch (Exception e) {
            logger.error("Error updating seed value to: {}", seedValue, e);
            throw e;
        }
    }
}
