package edu.stanford.protege.webprotege.identity.project;


import edu.stanford.protege.webprotege.ipc.EventHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class NewProjectEventHandler implements EventHandler<NewProjectEvent> {


    private final ProjectService projectService;

    public NewProjectEventHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Nonnull
    @Override
    public String getChannelName() {
        return NewProjectEvent.CHANNEL;
    }

    @Nonnull
    @Override
    public String getHandlerName() {
        return NewProjectEventHandler.class.getName();
    }

    @Override
    public Class<NewProjectEvent> getEventClass() {
        return NewProjectEvent.class;
    }

    @Override
    public void handleEvent(NewProjectEvent event) {
        throw new RuntimeException("Method not supported");
    }
    @Override
    public void handleEvent(NewProjectEvent event, ExecutionContext executionContext) {
        this.projectService.handleNewProjectCreated(event.projectId(), executionContext);
    }
}
