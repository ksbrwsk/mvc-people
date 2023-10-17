package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "mvc-people",
        version = "1.0",
        description = "Spring MVC CRUD Example Sample documents"
))
public class MvcPeopleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcPeopleApplication.class, args);
    }

}
