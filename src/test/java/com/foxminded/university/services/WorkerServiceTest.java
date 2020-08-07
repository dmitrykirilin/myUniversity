package com.foxminded.university.services;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.util.UniversityAgregator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/University-and-drop.sql")
@SpringBootTest
class WorkerServiceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UniversityAgregator universityAgregator;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private UniversityService universityService;

    @BeforeEach
    void setUp() throws PSQLException {
        universityAgregator.build();
        scheduleService.createRandomSchedule();
    }

    @Test
    void gettingAllEmptyIntervalsInScheduleTest() {
        List<ScheduleItem>[] weekEmptyIntervalsSchedule = workerService.getWeekSchedule();
        List<ScheduleItem> fullSchedule = scheduleService.getFullSchedule();
        boolean actual = true;
        for (List<ScheduleItem> daySchedule : weekEmptyIntervalsSchedule) {
            for (ScheduleItem item : daySchedule) {
                if(fullSchedule.stream().anyMatch(x -> x.getDayOfWeek().equals(item.getDayOfWeek())&&
                        x.getClassroom().equals(item.getClassroom())&&
                        x.getLessonNumber().equals(item.getLessonNumber()))){
                    actual = false;
                }

            }
        }
        assertTrue(actual);
    }

    @Test
    void gettingClassroomWeekScheduleTest() {
        Classroom classroom = universityService.getClassrooms().get(0);
        assertTrue(Arrays.stream(workerService.getClassroomWeekSchedule(classroom)).allMatch(x -> x.stream().allMatch(y -> y.getClassroom().equals(classroom))));
    }
}