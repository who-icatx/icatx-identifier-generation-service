package edu.stanford.protege.webprotege.identity.ids;

import edu.stanford.protege.webprotege.identity.services.ReadWriteLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static edu.stanford.protege.webprotege.identity.ids.IdHelper.*;

@Service
public class IdGenerationService {

    private final Logger LOGGER = LoggerFactory.getLogger(IdGenerationService.class);

    private final SeedRepository seedRepository;

    private final IdentificationRepository identificationRepository;

    private static final String SEED_NAME = "id_seed";
    private volatile long lastSeedValue = -1;
    private final Set<String> existingIdsCache = ConcurrentHashMap.newKeySet();

    private final ReadWriteLockService readWriteLock;

    public IdGenerationService(SeedRepository seedRepository,
                               IdentificationRepository identificationRepository,
                               ReadWriteLockService readWriteLock) {
        this.seedRepository = seedRepository;
        this.identificationRepository = identificationRepository;
        this.readWriteLock = readWriteLock;
    }

    public String generateUniqueId(String prefix) {
            long seedValue = getSeedValue();
            String uniqueId;

            do {
                seedValue++;
                uniqueId = String.format("%s%s", prefix, extractNineDigitNumberInStringFromHash(hashSeed(seedValue)));
                LOGGER.info("Trying to generate id " + uniqueId);
            } while (identificationRepository.existsById(uniqueId));

            identificationRepository.saveId(uniqueId);

            updateSeedValue(seedValue);
            return uniqueId;

    }


    private synchronized long getSeedValue() {
        if (lastSeedValue == -1) {
            lastSeedValue = seedRepository.findById(SEED_NAME).orElse(new Seed(SEED_NAME, 0)).getValue();
        }
        return lastSeedValue;
    }

    private void updateSeedValue(long seedValue) {
        lastSeedValue = seedValue;
        readWriteLock.executeWriteLock(() -> seedRepository.save(new Seed(SEED_NAME, seedValue)));
    }


}
