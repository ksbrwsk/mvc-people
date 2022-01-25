package de.ksbrwsk.people;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "people")
public class Person {

    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 10)
    private String name;

    public Person(String name) {
        this.name = name;
    }
}
