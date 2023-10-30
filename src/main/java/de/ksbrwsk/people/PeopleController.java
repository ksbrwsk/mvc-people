package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;

@RestController
@RequestMapping(value = API)
@RequiredArgsConstructor
@Slf4j
public class PeopleController {
    private final PersonRepository personRepository;

    @Operation(summary = "Get all people")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the people",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Person.class)
                            ))
                    }
            )})
    @GetMapping
    public ResponseEntity<List<Person>> handleFindAll() {
        List<Person> all = this.personRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Get a person by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the person",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Person not found",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public ResponseEntity<Person> handleFindById(@NotNull @PathVariable
                                                 @Parameter(description = "id of person to be searched") String id) {
        Optional<Person> personOptional = this.personRepository.findById(id);
        return personOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a person by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "person successfully deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Person not found",
                    content = @Content)})
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> handleDeleteById(@NotNull @PathVariable
                                                 @Parameter(description = "id of person to be searched") String id) {
        Optional<Person> personOptional = this.personRepository.findById(id);
        if (personOptional.isPresent()) {
            this.personRepository.delete(personOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "person successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<Person> handleCreate(@NotNull @Valid @RequestBody
                                               @Parameter(description = "new person data") Person person) {
        Person result = this.personRepository.save(person);
        return ResponseEntity.created(URI.create(API + "/" + result.getId()))
                .body(result);
    }

    @Operation(summary = "Update a person by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "person was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Person not found",
                    content = @Content)})
    @PutMapping(value = "/{id}")
    public ResponseEntity<Person> handleUpdate(@NotNull @PathVariable
                                               @Parameter(description = "id of person to be searched") String id,
                                               @NotNull @Valid @RequestBody
                                               @Parameter(description = "new person data") Person person) {
        Optional<Person> personOptional = this.personRepository.findById(id);
        if (personOptional.isPresent()) {
            Person old = personOptional.get();
            old.setName(person.getName());
            Person saved = this.personRepository.save(old);
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
