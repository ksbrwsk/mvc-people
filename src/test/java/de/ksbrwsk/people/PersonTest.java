package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {

    @Test
    void create() {
        Person person = new Person("4711", "Name");
        assertEquals("4711", person.getId());
        assertEquals("Name", person.getName());
    }
}