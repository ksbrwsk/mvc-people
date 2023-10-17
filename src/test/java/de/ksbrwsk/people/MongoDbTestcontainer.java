package de.ksbrwsk.people;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoDbTestcontainer {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:7.0.2")
            .withReuse(true);

}
