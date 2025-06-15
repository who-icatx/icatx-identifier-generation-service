package edu.stanford.protege.webprotege.identity.ids;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;

import java.util.*;

import static edu.stanford.protege.webprotege.identity.ids.IdHelper.*;

@Service
public class IdGenerationService {

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
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3)
    public String generateUniqueId(String prefix) {
        long seedValue = getSeedValue();
        String uniqueId;

        do {
            seedValue++;
            uniqueId = String.format("%s%s", prefix, extractNineDigitNumberInStringFromHash(hashSeed(seedValue)));
        } while (identificationRepository.exists(uniqueId));

        identificationRepository.saveListInPages(List.of(uniqueId));
        updateSeedValue(seedValue);
        return uniqueId;
    }

    private long getSeedValue() {
        if (lastSeedValue == -1) {
            lastSeedValue = seedRepository.findById(SEED_NAME)
                .orElse(new Seed(SEED_NAME, 0))
                .getValue();
        }
        return lastSeedValue;
    }

    private void updateSeedValue(long seedValue) {
        lastSeedValue = seedValue;
        seedRepository.save(new Seed(SEED_NAME, seedValue));
    }
}
