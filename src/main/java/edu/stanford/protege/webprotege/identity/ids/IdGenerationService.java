package edu.stanford.protege.webprotege.identity.ids;

import edu.stanford.protege.webprotege.identity.services.ReadWriteLockService;
import org.springframework.stereotype.Service;

import java.util.*;

import static edu.stanford.protege.webprotege.identity.ids.IdHelper.*;

@Service
public class IdGenerationService {

    private final SeedRepository seedRepository;
    private final IdentificationRepository identificationRepository;
    private static final String SEED_NAME = "id_seed";
    private volatile long lastSeedValue = -1;
    private final ReadWriteLockService readWriteLock;

    public IdGenerationService(SeedRepository seedRepository,
                               IdentificationRepository identificationRepository,
                               ReadWriteLockService readWriteLock) {
        this.seedRepository = seedRepository;
        this.identificationRepository = identificationRepository;
        this.readWriteLock = readWriteLock;
    }

    public String generateUniqueId(String prefix) {
        return readWriteLock.executeWriteLock(() -> {
            long seedValue = getSeedValue();
            String uniqueId;

            do {
                seedValue++;
                uniqueId = String.format("%s%s", prefix, extractNineDigitNumberInStringFromHash(hashSeed(seedValue)));
            } while (identificationRepository.existsById(uniqueId));

            identificationRepository.saveListInPages(List.of(uniqueId));
            updateSeedValue(seedValue);
            return uniqueId;
        });
    }

    private synchronized long getSeedValue() {
        if (lastSeedValue == -1) {
            lastSeedValue = readWriteLock.executeReadLock(() -> seedRepository.findById(SEED_NAME).orElse(new Seed(SEED_NAME, 0)).getValue());
        }
        return lastSeedValue;
    }

    private void updateSeedValue(long seedValue) {
        lastSeedValue = seedValue;
        readWriteLock.executeWriteLock(() -> seedRepository.save(new Seed(SEED_NAME, seedValue)));
    }
}
