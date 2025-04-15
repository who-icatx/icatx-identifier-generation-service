package edu.stanford.protege.webprotege.identity.commands;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Request;

@JsonTypeName(GetUniqueIdRequest.CHANNEL)
public record GetUniqueIdRequest(
        @JsonProperty("prefix") String prefix
) implements Request<GetUniqueIdResponse> {

    public static final String CHANNEL = "icatx.identity.GetUniqueId";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
