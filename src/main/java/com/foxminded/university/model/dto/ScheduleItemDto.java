package com.foxminded.university.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(hidden = true)
public class ScheduleItemDto {
        private String dayOfWeek;
        private Integer lessonNumber;
        private Integer classroom;
        private Integer course;
        private Integer group;
        private Integer teacher;
}
