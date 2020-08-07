package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
@Table(name = "groups")
public class Group {

    @Schema(description = "Unique identifier of the Group.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @NotBlank
    @JsonView({Views.IdName.class})
    @Schema(example = "AB-34", description = "Name of group, must be match to definite pattern")
    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private Set<ScheduleItem> schedule;

}
