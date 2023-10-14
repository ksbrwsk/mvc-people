package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class PersonRepositoryTest extends PostgresTestContainer {

    @Autowired
    PersonRepository personRepository;

    private Person findFirst() {
        Optional<Person> first = this.personRepository.findTopByOrderByIdAsc();
        assertTrue(first.isPresent());
        return first.get();
    }

    @BeforeEach
    public void init() {
        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100 ; i++) {
            people.add(new Person("Person@"+i));
        }
        this.personRepository.deleteAll();
        List<Person> all = this.personRepository.saveAll(people);
        assertFalse(all.isEmpty());
        assertEquals(100L,all.size());
    }

    @Test
    void findById() {
        Person first = this.findFirst();
        Optional<Person> result = this.personRepository.findById(first.getId());
        assertTrue(result.isPresent());
        assertFalse(result.get().getId().isEmpty());
        assertEquals("Person@1", result.get().getName());
    }

    @Test
    void delete() {
        Person person = this.findFirst();
        this.personRepository.delete(person);
        long count = this.personRepository.count();
        assertEquals(count, 99L);
    }

    @Test
    void update() {
        Person first = this.findFirst();
        first.setName("Name");
        Person saved = this.personRepository.save(first);
        assertEquals(first.getId(),saved.getId());
        assertEquals("Name", saved.getName());
    }
}