package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.persistence.*;
import java.util.Set;


@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"number"})
@ToString(of = {"number"})
@Table(name = "classrooms")
public class Classroom {

    @Schema(description = "Unique identifier of the Classroom.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @Schema(example = "Auditorium 100", description = "Name of classroom")
    @JsonView({Views.IdName.class})
    private String number;


    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private Set<ScheduleItem> schedule;
}
