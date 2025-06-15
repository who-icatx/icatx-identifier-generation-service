package edu.stanford.protege.webprotege.identity.ids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdGenerationServiceTest {

    @Mock
    private SeedRepository seedRepository;

    @Mock
    private IdentificationRepository identificationRepository;

    private IdGenerationService idGenerationService;
    private final String prefix = "somePrefix/";

    @BeforeEach
    public void setUp() {
        when(seedRepository.findById("id_seed")).thenReturn(Optional.of(new Seed("id_seed", 0)));
        when(identificationRepository.exists(anyString())).thenReturn(false);
        idGenerationService = new IdGenerationService(seedRepository, identificationRepository);
    }

    @Test
    public void GIVEN_noExistingIds_WHEN_generateUniqueId_isCalled_THEN_validUniqueIdIsReturned() {
        String uniqueId = idGenerationService.generateUniqueId(prefix);

        assertNotNull(uniqueId);
        assertTrue(uniqueId.startsWith(prefix), "The ID should start with the prefix");
        String numericPart = uniqueId.substring(prefix.length());
        assertEquals(9, numericPart.length(), "The numeric part should have 9 digits");
        assertTrue(numericPart.matches("\\d{9}"), "The numeric part should be numeric");

        verify(identificationRepository).saveListInPages(argThat(list ->
                list.size() == 1 && list.contains(uniqueId)
        ));
    }

    @Test
    public void GIVEN_existingIds_WHEN_generateUniqueId_isCalled_THEN_onlyNewIdIsAdded() {
        String valueForSeedOne = IdHelper.extractNineDigitNumberInStringFromHash(IdHelper.hashSeed(1));
        String valueForSeedTwo = IdHelper.extractNineDigitNumberInStringFromHash(IdHelper.hashSeed(2));
        String existingId1 = prefix + valueForSeedOne;
        String existingId2 = prefix + valueForSeedTwo;
        Set<String> existingIds = Set.of(existingId1, existingId2);

        when(identificationRepository.exists(anyString()))
            .thenAnswer(invocation -> existingIds.contains(invocation.getArgument(0)));

        String uniqueId = idGenerationService.generateUniqueId(prefix);

        assertNotNull(uniqueId);
        assertFalse(existingIds.contains(uniqueId),
                "The new ID should not match any existing IDs");

        verify(identificationRepository).saveListInPages(argThat(list ->
                list.size() == 1 && list.contains(uniqueId)
        ));
    }

    @Test
    public void GIVEN_seedExists_WHEN_generateUniqueId_isCalled_THEN_seedIsUpdated() {
        idGenerationService.generateUniqueId(prefix);

        verify(seedRepository).save(argThat(seed ->
                seed.getName().equals("id_seed") && seed.getValue() > 0
        ));
    }

    @Test
    public void GIVEN_noCache_WHEN_generateUniqueId_isCalledMultipleTimes_THEN_idsAreUnique() {
        String uniqueId1 = idGenerationService.generateUniqueId(prefix);
        String uniqueId2 = idGenerationService.generateUniqueId(prefix);

        assertNotNull(uniqueId1);
        assertNotNull(uniqueId2);
        assertNotEquals(uniqueId1, uniqueId2, "Each generated ID should be unique");
    }

    @Test
    public void GIVEN_candidateAlreadyExists_WHEN_generateUniqueId_isCalled_THEN_nextCandidateIsUsed() {
        String valueForSeedOne = IdHelper.extractNineDigitNumberInStringFromHash(IdHelper.hashSeed(1));
        String existingId = prefix + valueForSeedOne;
        Set<String> existingIds = Set.of(existingId);

        when(identificationRepository.exists(anyString()))
            .thenAnswer(invocation -> existingIds.contains(invocation.getArgument(0)));

        String uniqueId = idGenerationService.generateUniqueId(prefix);

        assertNotEquals(existingId, uniqueId, "The generated ID should differ from the colliding candidate");

        verify(seedRepository).save(argThat(seed ->
                seed.getName().equals("id_seed") && seed.getValue() == 2
        ));
    }
}
