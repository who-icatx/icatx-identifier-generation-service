package edu.stanford.protege.webprotege.identity;

import org.junit.jupiter.api.extension.*;
import org.slf4j.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoTestExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(MongoTestExtension.class);

    private MongoDBContainer mongoDBContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        var imageName = DockerImageName.parse("mongo");
        mongoDBContainer = new MongoDBContainer(imageName)
                .withExposedPorts(27017, 27017);
        mongoDBContainer.start();

        var mappedHttpPort = mongoDBContainer.getMappedPort(27017);
        logger.info("MongoDB port 27017 is mapped to {}", mappedHttpPort);
        System.setProperty("spring.data.mongodb.port", Integer.toString(mappedHttpPort));

    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        mongoDBContainer.stop();
    }
}
