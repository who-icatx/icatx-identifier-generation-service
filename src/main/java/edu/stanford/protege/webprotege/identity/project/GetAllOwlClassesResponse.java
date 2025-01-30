package edu.stanford.protege.webprotege.identity.project;


import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;
import org.semanticweb.owlapi.model.IRI;

import java.util.List;

@JsonTypeName(GetAllOwlClassesRequest.CHANNEL)
public record GetAllOwlClassesResponse(List<IRI> owlClassList) implements Response {
}
