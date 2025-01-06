package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.ksbrwsk.people.Constants.API;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebFluxTest
class PeopleControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    PersonRepository personRepository;

    Consumer<EntityExchangeResult<byte[]>> PRINT = (x) -> {
        if (x.getResponseBody() != null) {
            System.out.printf("%S - %s", x.getStatus(), new String(x.getResponseBody(), StandardCharsets.UTF_8));
        } else {
            System.out.printf("%S - NO RESULT", x.getStatus());
        }
    };

    @Test
    void handleNotFound() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleFindAll() {
        when(this.personRepository.findAll())
                .thenReturn(List.of(
                        new Person("4711", "Name1"),
                        new Person("4712", "Name2")
                ));
        this.webTestClient
                .get()
                .uri(API)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].name")
                .isEqualTo("Name1")
                .jsonPath("$[1].name")
                .isEqualTo("Name2");
    }

    @Test
    void handleFindById() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        this.webTestClient
                .get()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Name"));
    }

    @Test
    void handleFindByIdNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.webTestClient
                .get()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleDeleteById() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        doNothing().when(personRepository).delete(any());
        this.webTestClient
                .delete()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleDeleteByIdNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.webTestClient
                .delete()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @Test
    void handleCreate() {
        when(this.personRepository.save(new Person("Name")))
                .thenReturn(new Person("4711", "Name"));
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person("Name"))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Name"));
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

    @Test
    void handleUpdate() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Old")));
        when(this.personRepository.save(new Person("4711", "Update")))
                .thenReturn(new Person("4711", "Update"));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", "Update"))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Update"));
    }

    @Test
    void handleUpdateNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", "Update"))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(PRINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handlePostValid(String name) {
        when(this.personRepository.save(new Person(name)))
                .thenReturn(new Person("4711", name));
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void handlePostInvalid(String name) {
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

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleUpdateValid(String name) {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Old")));
        when(this.personRepository.save(new Person("4711", name)))
                .thenReturn(new Person("4711", name));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", name))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void handleUpdateInvalid(String name) {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Old")));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", name))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(PRINT);
    }
}