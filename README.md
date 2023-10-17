# mvc-people

#### Simple Spring Boot MVC example.

**Prerequisites:**

* [Java 21](https://openjdk.net/)
* [Apache Maven](https:http://maven.apache.org/)
* [Docker](https://www.docker.com/)

Application properties can be configured in

```bash
/src/main/resources/application.yml
```

#### How to build and run

Type

```bash
mvn package
mvn spring-boot:run
```

**Themes:**

* Spring MVC
* Database Connectivity (MongoDB)
* Unit Testing
* Integration Testing with Testcontainers (MongoDB)
* Open API

Point your Browser to
```bash
http://localhost:8080/swagger-ui/index.html
```
to try out swagger ui, or
```bash
http://localhost:8080/api-docs
```
to browse the api in JSON format.