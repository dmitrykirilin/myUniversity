package com.foxminded.university.services;

import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.util.UniversityAgregator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/University-and-drop.sql")
@SpringBootTest
class TeacherServiceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UniversityAgregator universityAgregator;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private UniversityService universityService;

    @BeforeEach
    void setUp() throws PSQLException {
        universityAgregator.build();
        scheduleService.createRandomSchedule();
    }

    @Test
    void gettingWeekScheduleForTeacherTest() {
        Teacher teacher = universityService.getTeachers().get(0);
        boolean actual = true;
        for (List<ScheduleItem> x : teacherService.getWeekSchedule(teacher)) {
            if (!x.stream().allMatch(y -> y.getTeacher().equals(teacher))) {
                actual = false;
                break;
            }
        }
        assertTrue(actual);
    }

    @Test
    void gettingDayScheduleTest() {
        Teacher teacher = universityService.getTeachers().get(0);
        assertTrue(teacherService.getDaySchedule(teacher, LocalDate.parse("2020-07-06")).stream().allMatch(x -> x.getTeacher().equals(teacher)&&x.getDayOfWeek().equals("TUESDAY")));
    }
}