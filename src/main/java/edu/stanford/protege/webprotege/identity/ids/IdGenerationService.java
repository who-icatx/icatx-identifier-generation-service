package edu.stanford.protege.webprotege.identity.ids;

import edu.stanford.protege.webprotege.identity.services.ReadWriteLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static edu.stanford.protege.webprotege.identity.ids.IdHelper.generateNineDigitNumberFromSeed;

@Service
public class IdGenerationService {

    private final Logger LOGGER = LoggerFactory.getLogger(IdGenerationService.class);

    private final SeedRepository seedRepository;

    private final IdentificationRepository identificationRepository;

    private static final String SEED_NAME = "id_seed";

    public IdGenerationService(SeedRepository seedRepository,
                               IdentificationRepository identificationRepository) {
        this.seedRepository = seedRepository;
        this.identificationRepository = identificationRepository;
    }

    public synchronized String generateUniqueId(String prefix) {
            long seedValue = getSeedValue();
            String uniqueId;

            do {
                seedValue++;
                uniqueId = String.format("%s%s", prefix, generateNineDigitNumberFromSeed(seedValue));
                LOGGER.info("Trying to generate id " + uniqueId);
            } while (identificationRepository.existsById(uniqueId));
            updateSeedValue(seedValue);

            identificationRepository.saveId(uniqueId);
            return uniqueId;

    }


    private synchronized long getSeedValue() {
        return seedRepository.findById(SEED_NAME).orElse(new Seed(SEED_NAME, 0)).getValue();
    }

    private void updateSeedValue(long seedValue) {
        seedRepository.save(new Seed(SEED_NAME, seedValue));
    }


}
