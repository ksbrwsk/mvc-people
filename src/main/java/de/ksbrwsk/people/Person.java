package de.ksbrwsk.people;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @Schema(name = "id", description = "The person's id")
    private String id;

    @NotBlank
    @Size(min = 1, max = 10)
    @Schema(minLength = 1, maxLength = 10, nullable = false, name = "name", description = "The person's name")
    private String name;

    public Person(String name) {
        this.name = name;
    }
}
