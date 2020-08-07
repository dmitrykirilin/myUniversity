package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "schedule"})
@ToString(exclude = {"id", "schedule"})
@Table(name = "teachers")
public class Teacher{

    @Schema(description = "Unique identifier of the teacher.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @NotBlank
    @Size(max = 50, message = "Имя слишком длинное!")
    @JsonView({Views.IdName.class})
    @Schema(example = "Bob", description = "Name of teacher")
    private String firstName;

    @NonNull
    @NotBlank(message = "Фамилия не может быть пустой!")
    @Size(max = 50, message = "Фамилия слишком длинная!")
    @JsonView({Views.IdName.class})
    @Schema(example = "Mentorov", description = "Surname of teacher")
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    @Schema(hidden = true)
    private Faculty faculty;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private Set<ScheduleItem> schedule;

    public Teacher(String firstName, String lastName, Faculty faculty) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
    }

    @Schema(hidden = true)
    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }
}
