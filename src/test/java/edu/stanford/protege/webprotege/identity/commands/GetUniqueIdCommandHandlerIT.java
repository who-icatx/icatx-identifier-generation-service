package edu.stanford.protege.webprotege.identity.commands;

import edu.stanford.protege.webprotege.identity.*;
import edu.stanford.protege.webprotege.identity.ids.*;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({IcatxIdentityGenerationServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, MongoTestExtension.class, RabbitTestExtension.class})
@ActiveProfiles("test")
public class GetUniqueIdCommandHandlerIT {

    @Autowired
    private GetUniqueIdCommandHandler commandHandler;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String prefix = "somePrefix/";


    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(OwlId.class);
        mongoTemplate.dropCollection(Seed.class);
    }

    @Test
    public void GIVEN_validRequest_WHEN_handleRequestCalled_THEN_returnsUniqueIdResponse() {
        GetUniqueIdRequest request = new GetUniqueIdRequest(prefix);
        ExecutionContext context = new ExecutionContext();

        GetUniqueIdResponse response = commandHandler.handleRequest(request, context).block();

        assertNotNull(response, "Response should not be null");
        String uniqueId = response.uniqueId();
        assertNotNull(uniqueId, "Unique ID should not be null");
        assertTrue(uniqueId.startsWith(prefix), "Unique ID should start with the given prefix");
        String numericPart = uniqueId.substring(prefix.length());
        assertEquals(9, numericPart.length(), "The numeric part should have 9 digits");
        assertTrue(numericPart.matches("\\d{9}"), "The numeric part should be numeric");
    }

    @Test
    public void GIVEN_multipleValidRequests_WHEN_handleRequestCalled_THEN_returnsDifferentUniqueIds() {
        GetUniqueIdRequest request1 = new GetUniqueIdRequest(prefix);
        GetUniqueIdRequest request2 = new GetUniqueIdRequest(prefix);
        ExecutionContext context = new ExecutionContext();

        GetUniqueIdResponse response1 = commandHandler.handleRequest(request1, context).block();
        GetUniqueIdResponse response2 = commandHandler.handleRequest(request2, context).block();

        assertNotNull(response1, "Response 1 should not be null");
        assertNotNull(response2, "Response 2 should not be null");
        assertNotEquals(response1.uniqueId(), response2.uniqueId(), "Each generated ID should be unique");
    }

    @Test
    public void GIVEN_validRequest_WHEN_handleRequestCalled_THEN_generatedIdIsPersistedInMongo() {
        GetUniqueIdRequest request = new GetUniqueIdRequest(prefix);
        ExecutionContext context = new ExecutionContext();

        GetUniqueIdResponse response = commandHandler.handleRequest(request, context).block();
        assertNotNull(response, "Response should not be null");
        String uniqueId = response.uniqueId();
        assertNotNull(uniqueId, "Unique ID should not be null");

        Query query = new Query(Criteria.where("value").is(uniqueId));
        long count = mongoTemplate.count(query, OwlId.class, OwlId.IDS_COLLECTION);
        assertTrue(count > 0, "The unique ID should be persisted in the database");
    }
}
