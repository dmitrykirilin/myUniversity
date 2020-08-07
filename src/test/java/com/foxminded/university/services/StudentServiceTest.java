package com.foxminded.university.services;

import com.foxminded.university.model.Group;
import com.foxminded.university.model.ScheduleItem;
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
class StudentServiceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UniversityAgregator universityAgregator;
    @Autowired
    private StudentService studentService;
    @Autowired
    private UniversityService universityService;

    @BeforeEach
    void setUp() throws PSQLException {
        universityAgregator.build();
        scheduleService.createRandomSchedule();
    }

    @Test
    void gettingWeekScheduleForGroupTest() {
        Group group = universityService.getGroups().get(0);
        boolean actual = true;
        for (List<ScheduleItem> x : studentService.getWeekSchedule(group)) {
            if (!x.stream().allMatch(y -> y.getGroup().equals(group))) {
                actual = false;
                break;
            }
        }
        assertTrue(actual);
    }

    @Test
    void gettingDayScheduleTest() {
        Group group = universityService.getGroups().get(0);
        assertTrue(studentService.getDaySchedule(group, LocalDate.parse("2020-07-06")).stream().allMatch(x -> x.getGroup().equals(group)&&x.getDayOfWeek().equals("TUESDAY")));
    }
}