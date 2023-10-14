package de.ksbrwsk.people;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PersonRepository extends MongoRepository<Person, String> {
    Optional<Person> findTopByOrderByIdAsc();
}
