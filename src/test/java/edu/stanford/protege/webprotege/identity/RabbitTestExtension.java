package edu.stanford.protege.webprotege.identity;

import org.junit.jupiter.api.extension.*;
import org.slf4j.*;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class RabbitTestExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(RabbitTestExtension.class);

    private RabbitMQContainer rabbitContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        var imageName = DockerImageName.parse("rabbitmq:3.7.25-management-alpine");
        rabbitContainer = new RabbitMQContainer(imageName)
                .withExposedPorts(5672);
        rabbitContainer.start();

        System.setProperty("spring.rabbitmq.host", rabbitContainer.getHost());
        System.setProperty("spring.rabbitmq.port", String.valueOf(rabbitContainer.getAmqpPort()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        rabbitContainer.stop();
    }
}
