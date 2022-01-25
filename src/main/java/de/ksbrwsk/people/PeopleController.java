package de.ksbrwsk.people;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PeopleController {
    private final PersonRepository personRepository;

    @GetMapping(API)
    public ResponseEntity<List<Person>> handleFindAll() {
        List<Person> all = this.personRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping(API + "/{id}")
    public ResponseEntity<Person> handleFindById(@NotNull @PathVariable String id) {
        Optional<Person> personOptional = this.personRepository.findById(id);
        if (personOptional.isPresent()) {
            return ResponseEntity.ok(personOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(API + "/{id}")
    public ResponseEntity<String> handleDeleteById(@NotNull @PathVariable String id) {
        Optional<Person> personOptional = this.personRepository.findById(id);
        if (personOptional.isPresent()) {
            this.personRepository.delete(personOptional.get());
            return ResponseEntity.ok("successfully deleted!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(API)
    public ResponseEntity<Person> handleCreate(@NotNull @Valid @RequestBody Person person) {
        Person result = this.personRepository.save(person);
        return ResponseEntity.created(URI.create(API + "/" + result.getId()))
                .body(result);
    }

    @PutMapping(API+"/{id}")
    public ResponseEntity<Person> handleUpdate(@PathVariable String id, @NotNull @Valid @RequestBody Person person) {
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
