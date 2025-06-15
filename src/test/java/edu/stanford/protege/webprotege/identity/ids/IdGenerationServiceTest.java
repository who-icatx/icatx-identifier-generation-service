package edu.stanford.protege.webprotege.identity.ids;

import edu.stanford.protege.webprotege.identity.services.ReadWriteLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IdGenerationServiceTest {

    @Mock
    private SeedRepository seedRepository;

    @Mock
    private IdentificationRepository identificationRepository;

    @Mock
    private ReadWriteLockService readWriteLock;

    private IdGenerationService idGenerationService;
    private final String prefix = "somePrefix/";


    @BeforeEach
    public void setUp() {
        when(readWriteLock.executeWriteLock(any(Callable.class))).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });
        when(readWriteLock.executeReadLock(any(Callable.class))).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        when(seedRepository.findById("id_seed")).thenReturn(Optional.of(new Seed("id_seed", 0)));

        doNothing().when(identificationRepository).saveListInPages(any());

        idGenerationService = new IdGenerationService(seedRepository, identificationRepository, readWriteLock);
    }

    @Test
    public void GIVEN_noExistingIds_WHEN_generateUniqueId_isCalled_THEN_validUniqueIdIsReturned() {
        when(identificationRepository.getExistingIds()).thenReturn(Collections.emptyList());

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

        when(identificationRepository.getExistingIds()).thenReturn(
                Arrays.asList(prefix + valueForSeedOne, prefix + valueForSeedTwo)
        );
        lenient().when(identificationRepository.existsById(prefix + valueForSeedOne)).thenReturn(true);
        lenient().when(identificationRepository.existsById(prefix + valueForSeedTwo)).thenReturn(true);

        String uniqueId = idGenerationService.generateUniqueId(prefix);

        assertNotNull(uniqueId);
        assertFalse(Arrays.asList(prefix + valueForSeedOne, prefix + valueForSeedTwo).contains(uniqueId),
                "The new ID should not match any existing IDs");

        verify(identificationRepository).saveListInPages(argThat(list ->
                list.size() == 1 && list.contains(uniqueId)
        ));
    }

    @Test
    public void GIVEN_seedExists_WHEN_generateUniqueId_isCalled_THEN_seedIsUpdated() {
        when(identificationRepository.getExistingIds()).thenReturn(Collections.emptyList());

        String uniqueId = idGenerationService.generateUniqueId(prefix);

        verify(seedRepository).save(argThat(seed ->
                seed.getName().equals("id_seed") && seed.getValue() > 0
        ));
    }

    @Test
    public void GIVEN_noCache_WHEN_generateUniqueId_isCalledMultipleTimes_THEN_idsAreUnique() {
        when(identificationRepository.getExistingIds()).thenReturn(Collections.emptyList());

        String uniqueId1 = idGenerationService.generateUniqueId(prefix);
        String uniqueId2 = idGenerationService.generateUniqueId(prefix);

        assertNotNull(uniqueId1);
        assertNotNull(uniqueId2);
        assertNotEquals(uniqueId1, uniqueId2, "Each generated ID should be unique");
    }

    @Test
    public void GIVEN_candidateAlreadyExists_WHEN_generateUniqueId_isCalled_THEN_nextCandidateIsUsed() {
        // first value for seedValue=1
        String valueForSeedOne = IdHelper.extractNineDigitNumberInStringFromHash(IdHelper.hashSeed(1));

        when(identificationRepository.getExistingIds()).thenReturn(List.of(prefix+valueForSeedOne));
        lenient().when(identificationRepository.existsById(prefix + valueForSeedOne)).thenReturn(true);

        // because we already have a value for seedValue=1 then it will need to generate for seedValue=2
        String uniqueId = idGenerationService.generateUniqueId(prefix);

        // here we make sure it generate with seedValue=2 and it did not keep the value for seedValue=1
        assertNotEquals(valueForSeedOne, uniqueId, "The generated ID should differ from the colliding candidate");

        // check that we save the latest seedValue
        verify(seedRepository).save(argThat(seed ->
                seed.getName().equals("id_seed") && seed.getValue() == 2
        ));
    }
}
