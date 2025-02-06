package edu.stanford.protege.webprotege.identity.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.identity.ids.OwlId;
import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesRequest;
import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesResponse;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.impl.CommandExecutorImpl;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.util.concurrent.locks.*;

@Configuration
public class ApplicationBeans {

    @Bean
    CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> executorForPostCoordination() {
        return new CommandExecutorImpl<>(GetAllOwlClassesResponse.class);
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("idGenerationModule");
        module.addDeserializer(OwlId.class, new OwlIdDeserializer());
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return objectMapper;
    }

    @Bean
    public ReadWriteLock readWriteLock() {
        return new ReentrantReadWriteLock(true);
    }
}
