package edu.stanford.protege.webprotege.identity.commands;


import edu.stanford.protege.webprotege.identity.ids.IdGenerationService;
import edu.stanford.protege.webprotege.ipc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

@WebProtegeHandler
public class GetUniqueIdCommandHandler implements CommandHandler<GetUniqueIdRequest, GetUniqueIdResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GetUniqueIdCommandHandler.class);
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
        try {
            String uniqueId = idGenerationService.generateUniqueId(request.prefix());
            var response = new GetUniqueIdResponse(uniqueId);
            return Mono.just(response);
        }catch (Exception e) {
            LOGGER.error("Error generating unique id ", e);
            throw new RuntimeException(e);
        }
    }
}