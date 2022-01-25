package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    public void init() {
        this.personRepository.deleteAll();
    }

    @Test
    void persist() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Name1"));
        people.add(new Person("Name2"));
        List<Person> people1 = this.personRepository.saveAll(people);
        assertFalse(people.isEmpty());
        assertEquals(2, people1.size());
    }

    @Test
    void findById() {
        Person person = this.personRepository.save(new Person("Name"));
        Optional<Person> result = this.personRepository.findById(person.getId());
        assertTrue(result.isPresent());
        assertFalse(result.get().getId().isEmpty());
        assertEquals("Name", result.get().getName());
    }

    @Test
    void delete() {
        Person person = this.personRepository.save(new Person("Name"));
        this.personRepository.delete(person);
        long count = this.personRepository.count();
        assertEquals(count, 0L);
    }
}