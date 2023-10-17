package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.ksbrwsk.people.Constants.API;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebIntegrationTest extends MongoDbTestcontainer {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PersonRepository personRepository;

    Consumer<EntityExchangeResult<byte[]>> PRINT = (x) -> {
        if (x.getResponseBody() != null) {
            System.out.printf("%S - %s", x.getStatus(), new String(x.getResponseBody(), StandardCharsets.UTF_8));
        } else {
            System.out.printf("%S - NO RESULT", x.getStatus());
        }
    };

    @BeforeEach
    void setUp() {
        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            people.add(new Person("Person@" + i));
        }
        this.personRepository.deleteAll();
        List<Person> all = this.personRepository.saveAll(people);
        assertThat(all.isEmpty()).isFalse();
        assertThat(all.size()).isEqualTo(100L);
    }

    private Person fetchFirst() {
        Optional<Person> person = this.personRepository.findTopByOrderByIdAsc();
        return person.orElseThrow();
    }

    @Test
    void handleNotFound() {
        this.webTestClient
                .get()
                .uri("/api/pepl")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleFindAll() {
        this.webTestClient
                .get()
                .uri(API)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].id").exists()
                .jsonPath("$.[0].name").isEqualTo("Person@1")
                .jsonPath("$.[1].id").exists()
                .jsonPath("$.[1].name").isEqualTo("Person@2");
    }

    @Test
    void handleFindById() {
        Person first = this.fetchFirst();
        this.webTestClient
                .get()
                .uri(API + "/" + first.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(first.getId())
                .jsonPath("$.name").isEqualTo(first.getName());
    }

    @Test
    void handleFindByIdNotFound() {
        this.webTestClient
                .get()
                .uri(API + "/99999999999")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleDeleteById() {
        Person first = this.fetchFirst();
        this.webTestClient
                .delete()
                .uri(API + "/" + first.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$")
                .isEqualTo("successfully deleted!");
    }

    @Test
    void handleDeleteByIdNotFound() {
        this.webTestClient
                .delete()
                .uri(API + "/9999999")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleUpdateValid(String name) {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(new Person(first.getId(), name))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(first.getId())
                .jsonPath("$.name").isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"00123456789"})
    @NullAndEmptySource
    void handleUpdateInvalid(String name) {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(new Person(first.getId(), name))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleUpdateNotFound() {
        this.webTestClient
                .put()
                .uri(API + "/9999999")
                .bodyValue(new Person("9999999", "Update"))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleUpdateNull() {
        Person first = this.fetchFirst();
        this.webTestClient
                .put()
                .uri(API + "/" + first.getId())
                .bodyValue(Optional.empty())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(PRINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleCreateValid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"00123456789"})
    @NullAndEmptySource
    void handleCreateInvalid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleCreateBadRequest() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(Optional.empty())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(PRINT);
    }
}
