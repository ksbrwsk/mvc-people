package de.ksbrwsk.people;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PersonRepositoryTest extends MongoDbTestcontainer {

    @Autowired
    PersonRepository personRepository;

    private Person findFirst() {
        Optional<Person> first = this.personRepository.findTopByOrderByIdAsc();
        assertThat(first.isPresent()).isTrue();
        return first.get();
    }

    @BeforeEach
    public void init() {
        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            people.add(new Person("Person@" + i));
        }
        this.personRepository.deleteAll();
        List<Person> all = this.personRepository.saveAll(people);
        assertThat(all.isEmpty()).isFalse();
        assertThat(all.size()).isEqualTo(100L);
    }

    @Test
    void findById() {
        Person first = this.findFirst();
        Optional<Person> result = this.personRepository.findById(first.getId());
        assertThat(result.isPresent()).isTrue();
        Person person = result.get();
        assertThat(person.getId()).isNotBlank();
        assertThat(person.getName()).isEqualTo("Person@1");
    }

    @Test
    void delete() {
        Person person = this.findFirst();
        this.personRepository.delete(person);
        long count = this.personRepository.count();
        assertThat(count).isEqualTo(99L);
    }

    @Test
    void update() {
        Person first = this.findFirst();
        first.setName("Name");
        Person saved = this.personRepository.save(first);
        assertThat(saved.getId()).isEqualTo(first.getId());
        assertThat(saved.getName()).isEqualTo("Name");
    }
}