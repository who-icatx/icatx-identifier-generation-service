package edu.stanford.protege.webprotege.identity.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.EventId;
import edu.stanford.protege.webprotege.common.ProjectEvent;
import edu.stanford.protege.webprotege.common.ProjectId;

@JsonTypeName(NewProjectEvent.CHANNEL)
public record NewProjectEvent(ProjectId projectId, EventId eventId) implements ProjectEvent {

    public final static String CHANNEL = "webprotege.events.projects.NewProjectEvent";

    @Override
    public ProjectId projectId() {
        return projectId;
    }


    @Override
    public EventId eventId() {
        return eventId;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
