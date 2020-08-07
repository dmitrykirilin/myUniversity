package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"name", "hoursPerWeek"})
@ToString(of = {"name", "hoursPerWeek"})
@Table(name = "courses")
public class Course {

    @Schema(description = "Unique identifier of the Course.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @NotBlank
    @Length(min = 3, max = 25, message = "Название курса от 3 до 25 символов!")
    @JsonView({Views.IdName.class})
    @Schema(example = "New course", description = "Name of subject")
    private String name;

    @NonNull
    @NotNull
    @Min(value = 3) @Max(value = 7)
    @JsonView({Views.FullSchedule.class})
    @Schema(example = "5", description = "Hours of course in one group per week")
    private Integer hoursPerWeek;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private Set<ScheduleItem> schedule;
}
