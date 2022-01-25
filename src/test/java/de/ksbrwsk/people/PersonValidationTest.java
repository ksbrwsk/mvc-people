package de.ksbrwsk.people;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PersonValidationTest {

    @Autowired
    Validator validator;

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void valid(String name) {
        Person person = new Person("4711", name);
        Set<ConstraintViolation<Person>> violations = this.validator.validate(person);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void invalid(String name) {
        Person person = new Person("4711", name);
        Set<ConstraintViolation<Person>> violations = this.validator.validate(person);
        assertFalse(violations.isEmpty());
    }
}