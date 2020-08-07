package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "faculty"})
@ToString(of = {"firstName", "lastName"})
@Table(name = "students")
public class Student{

    @Schema(description = "Unique identifier of the Student.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @NotBlank
    @Size(max = 50, message = "Имя слишком длинное!")
    @JsonView({Views.IdName.class})
    @Schema(example = "Bob", description = "Name of student")
    private String firstName;

    @NonNull
    @NotBlank
    @Size(max = 50, message = "Фамилия слишком длинная!")
    @Schema(example = "Studentov", description = "Surname of student")
    @JsonView({Views.IdName.class})
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    @JsonView({Views.FullSchedule.class})
    @Schema(example = "1", description = "Id of group", implementation = Integer.class)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    @Schema(hidden = true)
    private Faculty faculty;

    public Student(String firstName, String lastName, Group group, Faculty faculty) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.faculty = faculty;
    }

    public Student(String firstName, String lastName, Group group) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
    }

    @Schema(hidden = true)
    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }
}
