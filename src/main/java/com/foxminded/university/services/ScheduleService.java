package com.foxminded.university.services;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ClassroomRepo classroomJPA;
    private final CourseRepo courseJPA;
    private final GroupRepo groupJPA;
    private final ScheduleItemRepo scheduleItemJPA;
    private final TeacherRepo teacherJPA;

    private Random rnd = new Random();


    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public List<ScheduleItem> createRandomSchedule() {
        log.info("Schedule creating...");
        List<Course> courses = courseJPA.findAll();
        int groupsCount = groupJPA.findAll().size();
        int classroomAmount = classroomJPA.findAll().size();
        List<Teacher> teachers = teacherJPA.findAll();

        // adding random amount of random groups to each course in the university
        for (Course course : courses) {
            int groupAmount = rnd.nextInt(groupsCount) + 1;
            List<Integer> groupsOnCourse= new ArrayList<>();
            while (groupAmount > 0){
                Integer genGroupId = rnd.nextInt(groupsCount) + 1;
                if(groupsOnCourse.stream().noneMatch(x -> x.equals(genGroupId))){
                    groupsOnCourse.add(genGroupId);
                    groupAmount--;
                }
            }
            // adding random teacher to each group on course
            for (Integer groupId : groupsOnCourse) {
                int teacherId = rnd.nextInt(teachers.size()) + 1;

                // random lessons' distribution
                int limitedFlag = 0;
                for (int j = 0; j < course.getHoursPerWeek(); j++) {
                    int[] newGreedItem = getItemOfGreed(classroomAmount);
                    try {
                        scheduleItemJPA.save(new ScheduleItem(DayOfWeek.of(newGreedItem[0]).name(), newGreedItem[1], classroomJPA.findById(newGreedItem[2]).get(), course, groupJPA.findById(groupId).get(), teacherJPA.findById(teacherId).get()));
                        log.info("ScheduleItem joined!!");
                    } catch (DataIntegrityViolationException ex){
                        log.debug("ConstraintViolationException - " + ex.getMessage());
                        if(limitedFlag > 40){
                            log.error("It is too hard to create schedule for this amount of courses and classrooms!");
                            throw new RuntimeException(ex.getMessage());
                        }else {
                            j--;
                            limitedFlag++;
                        }
                    }
                }
            }
        }
        log.info("Schedule has done.");
        List<ScheduleItem> schedule = scheduleItemJPA.findAll();
        schedule.sort(ScheduleItem.getComparator());
        return schedule;
    }


    public void deleteSchedule() {
        log.info("Removing all scheduleItems!");
        scheduleItemJPA.deleteAll();
    }

    public List<ScheduleItem> getFullSchedule(){
        List<ScheduleItem> schedule = scheduleItemJPA.findAll();
        schedule.sort(ScheduleItem.getComparator());
        return schedule;
    }


    @Transactional(readOnly = true)
    public List<List<ScheduleItem>> getDaySchedule(String dayOfWeek){
        List<Classroom> classrooms = classroomJPA.findAll();
        List<ScheduleItem> todaySchedule = getFullSchedule().stream().filter(x -> x.getDayOfWeek().equals(dayOfWeek)).collect(Collectors.toList());
        List<List<ScheduleItem>> schedule = new ArrayList<>();
        for (int i = 0; i < classrooms.size(); i++) {
            schedule.add(new ArrayList<>());
        }
        for (ScheduleItem scheduleItem : todaySchedule) {
            schedule.get(scheduleItem.getClassroom().getId() - 1).add(scheduleItem);
        }
        return schedule;
    }


    public Integer addScheduleItem(ScheduleItem item){
        Integer returnedId = 0;
        try {
            returnedId = scheduleItemJPA.save(item).getId();
            log.debug("Success! Added new ScheduleItem {}", item);
        }catch (DataIntegrityViolationException ex){
            log.info("Impossible, the classroom is already using at this time");
            return 0;
        }
        return returnedId;
    }


    public boolean removeScheduleItem(String dayOfWeek, Integer classroom, Integer lesson){
        List<ScheduleItem> scheduleItems = scheduleItemJPA.findAll();
        if(scheduleItems == null){
            log.info("Table schedule is empty!");
            return false;
        }
        Optional<ScheduleItem> item = scheduleItems.stream().filter(x -> x.getDayOfWeek().equals(dayOfWeek) && x.getLessonNumber().equals(lesson) && x.getClassroom().getId().equals(classroom)).findFirst();
        if(item.isPresent()){
            log.info("Removing scheduleItem!");
            scheduleItemJPA.delete(item.get());
            return true;
        }
        log.info("This lesson is not exist.");
        return false;
    }


    public void removeAllItemsByCourseId(Integer id){
        List<ScheduleItem> collect = getFullSchedule().stream().filter(x -> x.getCourse().getId().equals(id)).collect(Collectors.toList());
        collect.forEach(scheduleItemJPA::delete);
    }


    public void removeAllItemsByGroupId(Integer id){
        List<ScheduleItem> collect = getFullSchedule().stream().filter(x -> x.getGroup().getId().equals(id)).collect(Collectors.toList());
        collect.forEach(scheduleItemJPA::delete);
    }


    private int[] getItemOfGreed(int countOfClassrooms){
        int day = rnd.nextInt(6) + 1;
        int lesson = rnd.nextInt(6) + 1;
        int classroom = rnd.nextInt(countOfClassrooms) + 1;
        return new int[] {day, lesson, classroom};
    }


    public void removeAllItemsByTeacherId(Integer id) {
        List<ScheduleItem> collect = getFullSchedule().stream().filter(x -> x.getTeacher().getId().equals(id)).collect(Collectors.toList());
        collect.forEach(scheduleItemJPA::delete);
    }

    public void removeScheduleItem(Integer id) {
        scheduleItemJPA.deleteById(id);
    }

    public ScheduleItem getScheduleItemById(Integer id) {
        return scheduleItemJPA.findById(id).get();
    }
}
