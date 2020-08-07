package com.foxminded.university.services;

import com.foxminded.university.model.*;
import com.foxminded.university.repository.*;
import com.foxminded.university.util.GroupNameGenerator;
import com.foxminded.university.util.SqlFileExecutor;
import com.foxminded.university.util.StudentGenerator;
import com.foxminded.university.util.TeacherGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UniversityService {

    private final SqlFileExecutor executor;
    private final ClassroomRepo classroomJPA;
    private final CourseRepo courseJPA;
    private final FacultyRepo facultyJPA;
    private final GroupRepo groupJPA;
    private final StudentRepo studentJPA;
    private final TeacherRepo teacherJPA;

    private Random rnd = new Random();


    public void fillUniversityEntities(int countOfClassrooms, int countOfGroups, int countOfCourses, int countOfStudents, int countOfTeachers) throws PSQLException {
        facultyJPA.save(new Faculty("Mathematic"));
        facultyJPA.save(new Faculty("Philosofy"));
        log.info("All faculties added to DB");

        if (countOfCourses > 0) {
            List<Course> courses = getGeneratedCourses(countOfCourses);
            for (Course cours : courses) {
                courseJPA.save(cours);
            }
            log.info("All courses added to DB");
        }

        if (countOfGroups > 0){
            List<Group> groups = getGeneratedGroups(countOfGroups);
            for (Group group : groups) {
                groupJPA.save(group);
            }
            log.info("All groups added to DB");
        }

        if (countOfClassrooms > 0) {
            List<Classroom> classrooms = getGeneratedClassrooms(countOfClassrooms);
            for (Classroom classroom : classrooms) {
                classroomJPA.save(classroom);
            }
            log.info("All classrooms added to DB");
        }

        if (countOfStudents>0){
            List<Student> generatedStudents = new StudentGenerator().getPersons(countOfStudents);
            generatedStudents.forEach(x -> {
                x.setGroup(groupJPA.findById(rnd.nextInt(countOfGroups) + 1).get());
                x.setFaculty(facultyJPA.findById(rnd.nextInt(2) + 1).get());
            });
            for (Student generatedStudent : generatedStudents) {
                studentJPA.save(generatedStudent);
            }
            log.info("All students added to DB");
        }
        if (countOfTeachers > 0) {
            List<Teacher> generatedTeachers = new TeacherGenerator().getPersons(countOfTeachers);
            generatedTeachers.forEach(x -> x.setFaculty(facultyJPA.findById(rnd.nextInt(2) + 1).get()));
            for (Teacher generatedTeacher : generatedTeachers) {
                teacherJPA.save(generatedTeacher);
            }
            log.info("All teachers added to DB");
        }
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void removeAllEntities(){
        executor.execute("/University-and-drop.sql");
    }


    public Classroom getClassroomById(Integer classroomId){
        Optional<Classroom> classroom = classroomJPA.findById(classroomId);
        return classroom.orElse(null);
    }


    public void removeTeacherById(Integer id){
        teacherJPA.deleteById(id);
    }

    public Integer addCourse(Course entity) throws PSQLException {
        return courseJPA.save(entity).getId();
    }


    public Faculty getFacultyById(Integer id) {
        return facultyJPA.findById(id).get();
    }

    public Integer addGroup(Group entity) throws PSQLException {
        return groupJPA.save(entity).getId();
    }


    public Integer addClassroom(Classroom entity) throws PSQLException {
        return classroomJPA.save(entity).getId();
    }


    public Integer addStudent(Student student) throws PSQLException {
        return studentJPA.save(student).getId();
    }


    public Integer addTeacher(Teacher teacher) throws PSQLException {
        return teacherJPA.save(teacher).getId();
    }


    public List<Group> getGroups(){
        return groupJPA.findAll();
    }

    public List<Course> getCourses(){
        return courseJPA.findAll();
    }

    public List<Classroom> getClassrooms(){
        return classroomJPA.findAll();
    }

    public List<Student> getStudents(){
        return studentJPA.findAll();
    }

    public List<Teacher> getTeachers(){
        return teacherJPA.findAll();
    }

    private List<Group> getGeneratedGroups(int countOfGroups) {
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0; i < countOfGroups; i++) {
            groups.add(new Group(GroupNameGenerator.getRandomName()));
        }
        return groups;
    }

    private List<Classroom> getGeneratedClassrooms(int countOfClassrooms) {
        ArrayList<Classroom> classrooms = new ArrayList<>();
        for (int i = 1; i <= countOfClassrooms; i++) {
            classrooms.add(new Classroom(i + " auditorium"));
        }
        return classrooms;
    }

    private List<Course> getGeneratedCourses(int countOfCourses) {
        ArrayList<Course> courses = new ArrayList<>();
        String[] coursesNames = {
                "Math", "biology",
                "computer science", "dentistry",
                "engineering",	"geology",
                "medicine", "physics",
                "veterinary medicine", "economics"};
        for (int i = 0; i < countOfCourses; i++) {
            courses.add(new Course(coursesNames[i], rnd.nextInt(4) + 3));
        }
        return courses;
    }

    public void removeStudentById(Integer id) {
        studentJPA.deleteById(id);
    }

    public Teacher getTeacherById(Integer id) {
        return teacherJPA.findById(id).get();
    }

    public Teacher updateTeacher(Teacher teacher) {
        return teacherJPA.save(teacher);
    }

    public Student getStudentById(Integer id) {
        return studentJPA.findById(id).get();
    }

    public Student updateStudent(Student student) {
        return studentJPA.save(student);
    }

    public void removeCourseById(Integer id) {
        courseJPA.deleteById(id);
    }

    public Course getCourseById(Integer id) {
        return courseJPA.findById(id).get();
    }

    public Course updateCourse(Course course) {
        return courseJPA.save(course);
    }

    public void removeGroupById(Integer id) {
        groupJPA.deleteById(id);
    }

    public Group getGroupById(Integer id) {
        return groupJPA.findById(id).get();
    }

    public Group updateGroup(Group group) {
        return groupJPA.save(group);
    }

    public Group findGroupByName(String name) {
        return groupJPA.findByName(name);
    }

    public Course findCourseByName(String name) {
        return courseJPA.findByName(name);
    }

    public Student findStudentByFullName(String firstName, String lastName) {
        return studentJPA.findByFirstNameAndLastName(firstName, lastName);
    }

    public Teacher findTeacherByFullName(String firstName, String lastName) {
        return teacherJPA.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<Faculty> getFaculties() {
        return facultyJPA.findAll();
    }
}
