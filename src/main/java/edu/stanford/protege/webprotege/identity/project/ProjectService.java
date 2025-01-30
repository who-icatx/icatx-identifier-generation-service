package edu.stanford.protege.webprotege.identity.project;


import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.identity.config.SecurityContextHelper;
import edu.stanford.protege.webprotege.identity.ids.IdentificationRepository;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);


    private final CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> getAllClassesCommand;

    private final IdentificationRepository identificationRepository;

    public ProjectService(CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> getAllClassesCommand, IdentificationRepository identificationRepository) {
        this.getAllClassesCommand = getAllClassesCommand;
        this.identificationRepository = identificationRepository;
    }

    public List<String> registerProjects(List<String> projectIds) {
        List<String> response = new ArrayList<>();
        for(String projectId: projectIds) {
            try {
                handleNewProjectCreated(ProjectId.valueOf(projectId), SecurityContextHelper.getExecutionContext());
                response.add(projectId);
            } catch (Exception e) {
                LOGGER.error("Error registering project " + projectId,e);
            }
        }
        return response;
    }

    public void handleNewProjectCreated(ProjectId projectId, ExecutionContext executionContext) {
        try {
            List<String> newIds = getAllClassesCommand.execute(new GetAllOwlClassesRequest(projectId), executionContext)
                    .get()
                    .owlClassList().stream().map(IRI::toString).toList();
            List<String> existingIds = identificationRepository.getExistingIds();

            HashSet<String> existingIdsSet;

            if(existingIds == null || existingIds.isEmpty()) {
                existingIdsSet = new HashSet<>();
            } else {
                existingIdsSet = new HashSet<>(existingIds);
            }

            existingIdsSet.addAll(newIds);

            this.identificationRepository.saveListInPages(existingIdsSet.stream().toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
