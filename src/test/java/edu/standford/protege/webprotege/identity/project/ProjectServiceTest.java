package edu.standford.protege.webprotege.identity.project;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.identity.ids.IdentificationRepository;
import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesRequest;
import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesResponse;
import edu.stanford.protege.webprotege.identity.project.ProjectService;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.model.IRI;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> getAllClassesCommand;
    @Mock
    private IdentificationRepository identificationRepository;

    private ProjectService projectService;

    ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    @BeforeEach
    public void setUp(){
        projectService = new ProjectService(getAllClassesCommand, identificationRepository);
    }


    @Test
    public void GIVEN_newProject_WHEN_noEntriesExist_THEN_newProjectIdsAreSaved() throws ExecutionException, InterruptedException {
        IRI iri1 = IRI.generateDocumentIRI();
        IRI iri2 = IRI.generateDocumentIRI();
        var resp = new GetAllOwlClassesResponse(Arrays.asList(iri1, iri2));
        when(getAllClassesCommand.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> resp));

        projectService.handleNewProjectCreated(ProjectId.generate(), new ExecutionContext());

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
    public void GIVEN_newProject_WHEN_someEntriesAlreadyExist_THEN_onlyNewIdsAreAdded(){
        IRI iri1 = IRI.generateDocumentIRI();
        IRI iri2 = IRI.generateDocumentIRI();
        IRI iri3 = IRI.generateDocumentIRI();

        var resp = new GetAllOwlClassesResponse(Arrays.asList(iri1, iri2));
        when(getAllClassesCommand.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> resp));
        when(identificationRepository.getExistingIds()).thenReturn(Arrays.asList(iri2.toString(), iri3.toString()));

        projectService.handleNewProjectCreated(ProjectId.generate(), new ExecutionContext());

        // Assert
        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(identificationRepository).saveListInPages(argumentCaptor.capture()); // Capture the argument

        // Verify the captured values
        List<String> capturedIds = argumentCaptor.getValue();
        assertNotNull(capturedIds);
        assertEquals(3, capturedIds.size());
        assertTrue(capturedIds.contains(iri1.toString()));
        assertTrue(capturedIds.contains(iri2.toString()));
        assertTrue(capturedIds.contains(iri3.toString()));

    }

}
