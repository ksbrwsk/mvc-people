package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    void create() {
        Person person = new Person("4711", "Name");
        assertThat(person.getId()).isEqualTo("4711");
        assertThat(person.getName()).isEqualTo("Name");
    }
}