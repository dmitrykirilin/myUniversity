package com.foxminded.university.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView({Views.IdName.class})
@Schema(hidden = true)
public class UniversityEntities {
    List<Classroom> classrooms;
    List<Group> groups;
    List<Course> courses;
    List<Student> students;
    List<Teacher> teachers;
}
