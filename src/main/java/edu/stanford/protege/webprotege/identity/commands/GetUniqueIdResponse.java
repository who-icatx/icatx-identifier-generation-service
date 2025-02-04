package edu.stanford.protege.webprotege.identity.commands;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Response;


@JsonTypeName(GetUniqueIdRequest.CHANNEL)
public record GetUniqueIdResponse(@JsonProperty("uniqueId") String uniqueId) implements Response {
}
