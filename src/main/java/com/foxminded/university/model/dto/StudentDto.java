package com.foxminded.university.model.dto;

import com.foxminded.university.model.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(hidden = true)
public class StudentDto {
        private Student student;
        private Integer groupId;
}
