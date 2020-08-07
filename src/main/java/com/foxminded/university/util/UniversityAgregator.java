package com.foxminded.university.util;

import com.foxminded.university.services.UniversityService;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
@Slf4j
public class UniversityAgregator {

    @Autowired
    private UniversityService universityService;

    private int countOfClassrooms;

    private int countOfGroups;

    private int countOfCourses;

    private int countOfStudents;

    private int countOfTeachers;

    public void build() throws PSQLException {
        Properties config = new Properties();
        try (InputStream in = UniversityAgregator.class.getResourceAsStream("/application.properties");){
            config.load(in);
            this.countOfClassrooms = Integer.parseInt(config.getProperty("countOfClassrooms"));
            this.countOfGroups = Integer.parseInt(config.getProperty("countOfGroups"));
            this.countOfCourses = Integer.parseInt(config.getProperty("countOfCourses"));
            this.countOfStudents = Integer.parseInt(config.getProperty("countOfStudents"));
            this.countOfTeachers = Integer.parseInt(config.getProperty("countOfTeachers"));
            log.info("Data recieved: countOfClassrooms - {}, countOfClassrooms - {}, countOfGroups - {}, countOfStudents - {}, countOfTeachers - {}", countOfClassrooms, countOfGroups, countOfCourses, countOfStudents, countOfTeachers);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        universityService.fillUniversityEntities(countOfClassrooms, countOfGroups, countOfCourses, countOfStudents, countOfTeachers);
    }

}
