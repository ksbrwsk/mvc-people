package de.ksbrwsk.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class TestMvcPeopleApplication {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer("mongo:7.0.2")
                .withReuse(true);
    }

    public static void main(String[] args) {
        SpringApplication.from(MvcPeopleApplication::main)
                .with(TestMvcPeopleApplication.class)
                .run(args);
    }
}
