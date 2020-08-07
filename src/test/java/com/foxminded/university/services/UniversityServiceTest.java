package com.foxminded.university.services;

import com.foxminded.university.model.Course;
import com.foxminded.university.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Sql("/University-and-drop.sql")
@SpringBootTest
class UniversityServiceTest {

    @Autowired
    private UniversityService universityService;
    @Autowired
    private ClassroomRepo classroomJPA;
    @Autowired
    private CourseRepo courseJPA;
    @Autowired
    private GroupRepo groupJPA;
    @Autowired
    private StudentRepo studentJPA;
    @Autowired
    private TeacherRepo teacherJPA;

    private static int countOfClassrooms;

    private static int countOfGroups;

    private static int countOfCourses;

    private static int countOfStudents;

    private static int countOfTeachers;

    static {
        countOfClassrooms = 10;
        countOfGroups = 10;
        countOfCourses = 10;
        countOfStudents = 10;
        countOfTeachers = 10;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fillingUniversityEntitiesTest() throws PSQLException {
        universityService.fillUniversityEntities(countOfClassrooms, countOfGroups, countOfCourses, countOfStudents, countOfTeachers);
        assertEquals(countOfClassrooms, classroomJPA.findAll().size());
        assertEquals(countOfGroups, groupJPA.findAll().size());
        assertEquals(countOfCourses, courseJPA.findAll().size());
        assertEquals(countOfStudents, studentJPA.findAll().size());
        assertEquals(countOfTeachers, teacherJPA.findAll().size());
    }

    @Test
    void gettingGroupsAfterGeneratingTest() throws PSQLException {
        universityService.fillUniversityEntities(0, countOfGroups, 0, 0, 0);
        assertEquals(countOfGroups, universityService.getGroups().size());
    }

    @Test
    void addingNewWorkerTest() throws PSQLException {
        assertEquals(1, universityService.addCourse(new Course("Testing", 1000)));
    }
}