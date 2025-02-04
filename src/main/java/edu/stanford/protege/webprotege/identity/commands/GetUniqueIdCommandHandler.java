package edu.stanford.protege.webprotege.identity.commands;


import edu.stanford.protege.webprotege.identity.ids.IdGenerationService;
import edu.stanford.protege.webprotege.ipc.*;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

@WebProtegeHandler
public class GetUniqueIdCommandHandler implements CommandHandler<GetUniqueIdRequest, GetUniqueIdResponse> {

    private final IdGenerationService idGenerationService;

    public GetUniqueIdCommandHandler(IdGenerationService idGenerationService) {

        this.idGenerationService = idGenerationService;
    }

    @Nonnull
    @Override
    public String getChannelName() {
        return GetUniqueIdRequest.CHANNEL;
    }

    @Override
    public Class<GetUniqueIdRequest> getRequestClass() {
        return GetUniqueIdRequest.class;
    }

    @Override
    public Mono<GetUniqueIdResponse> handleRequest(GetUniqueIdRequest request, ExecutionContext executionContext) {
        String uniqueId = idGenerationService.generateUniqueId(request.prefix());
        var response = new GetUniqueIdResponse(uniqueId);

        return Mono.just(response);
    }
}