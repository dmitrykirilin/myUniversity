package com.foxminded.university.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(hidden = true)
public class RequestUniversityEntitiesDto {
    private Integer countOfClassrooms;
    private Integer countOfGroups;
    private Integer countOfCourses;
    private Integer countOfStudents;
    private Integer countOfTeachers;
}
