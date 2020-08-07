package com.foxminded.university.services;

import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.repository.ScheduleItemRepo;
import com.foxminded.university.util.UniversityAgregator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/University-and-drop.sql")
//@Sql( scripts = "/University-and-drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    private UniversityAgregator universityAgregator;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleItemRepo scheduleItemJPA;
    @Autowired
    private UniversityService universityService;


    @BeforeEach
    public void setUp() throws PSQLException {
        universityAgregator.build();
    }


    @Test
    void shouldCreateRandomScheduleTest() throws PSQLException {
        assertEquals(0, scheduleItemJPA.findAll().size());
       scheduleService.createRandomSchedule();
       assertFalse(scheduleItemJPA.findAll().isEmpty());
    }

    @Test
    void addScheduleItemIfExistsOrNotExistsTest(){
        Integer actual = scheduleService.addScheduleItem(new ScheduleItem("MONDAY", 1, universityService.getClassroomById(1), universityService.getCourseById(1), universityService.getGroupById(1), universityService.getTeacherById(1)));
        assertEquals(1, actual);
        actual = scheduleService.addScheduleItem(new ScheduleItem("MONDAY", 1, universityService.getClassroomById(1), universityService.getCourseById(1), universityService.getGroupById(1), universityService.getTeacherById(1)));
        assertEquals(0, actual);
    }

    @Test
    void removeScheduleItemIfExistsOrNotExistsTest(){
        assertFalse(scheduleService.removeScheduleItem("SUNDAY", 1, 1));
        scheduleService.addScheduleItem(new ScheduleItem("MONDAY", 1, universityService.getClassroomById(1), universityService.getCourseById(1), universityService.getGroupById(1), universityService.getTeacherById(1)));
        assertTrue(scheduleService.removeScheduleItem("MONDAY", 1, 1));
        assertEquals(0, scheduleItemJPA.findAll().size());
    }
}
