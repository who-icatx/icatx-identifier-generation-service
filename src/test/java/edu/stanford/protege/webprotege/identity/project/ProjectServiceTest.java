package edu.stanford.protege.webprotege.identity.project;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.identity.ids.IdentificationRepository;
import edu.stanford.protege.webprotege.ipc.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.model.IRI;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> getAllClassesCommand;
    @Mock
    private IdentificationRepository identificationRepository;

    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        projectService = new ProjectService(getAllClassesCommand, identificationRepository);
    }


    @Test
    public void GIVEN_newProject_WHEN_noEntriesExist_THEN_newProjectIdsAreSaved() {
        IRI iri1 = IRI.generateDocumentIRI();
        IRI iri2 = IRI.generateDocumentIRI();
        var resp = new GetAllOwlClassesResponse(Arrays.asList(iri1, iri2));
        when(getAllClassesCommand.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> resp));

        projectService.registerProject(ProjectId.generate(), new ExecutionContext());

        // Assert
        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(identificationRepository).saveListInPages(argumentCaptor.capture()); // Capture the argument

        // Verify the captured values
        List<String> capturedIds = argumentCaptor.getValue();
        assertNotNull(capturedIds);
        assertEquals(2, capturedIds.size());
        assertTrue(capturedIds.contains(iri1.toString()));
        assertTrue(capturedIds.contains(iri2.toString()));
    }

    @Test
    public void GIVEN_newProject_WHEN_someEntriesAlreadyExist_THEN_onlyNewIdsAreAdded() {
        IRI iri1 = IRI.generateDocumentIRI();
        IRI iri2 = IRI.generateDocumentIRI();
        IRI iri3 = IRI.generateDocumentIRI();

        var resp = new GetAllOwlClassesResponse(Arrays.asList(iri1, iri2));
        when(getAllClassesCommand.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> resp));
        when(identificationRepository.getExistingIds()).thenReturn(Arrays.asList(iri2.toString(), iri3.toString()));

        projectService.registerProject(ProjectId.generate(), new ExecutionContext());

        // Assert
        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(identificationRepository).saveListInPages(argumentCaptor.capture()); // Capture the argument

        // Verify the captured values
        List<String> capturedIds = argumentCaptor.getValue();
        assertNotNull(capturedIds);
        assertEquals(1, capturedIds.size());
        assertTrue(capturedIds.contains(iri1.toString()));

    }

}
