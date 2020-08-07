package com.foxminded.university.controllers.admin;

import com.foxminded.university.model.Lessons;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ScheduleManagementController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;

    @Value("${countOfClassrooms}")
    private Integer countOfClassrooms;
    @Value("${countOfGroups}")
    private Integer countOfGroups;
    @Value("${countOfCourses}")
    private Integer countOfCourses;
    @Value("${countOfStudents}")
    private Integer countOfStudents;
    @Value("${countOfTeachers}")
    private Integer countOfTeachers;


    @GetMapping("/admin")
    public String admin(@RequestParam(name = "error", required = false) Boolean error,
                        Model model){
        if(Boolean.TRUE.equals(error)){
            model.addAttribute("message", "Impossible to create schedule! Try reduce amount of courses or increase amount of classrooms!");
        }

        return "admin/mainAdmin";
    }

    @GetMapping("/admin/filling")
    public String fillUniversity(@RequestParam(name = "message", required = false) String message,
                                 Model model){
        if(!StringUtils.isEmpty(message)){
            model.addAttribute("message", "Университет обновлен!");
        }
        model.addAttribute("Classrooms", universityService.getClassrooms());
        model.addAttribute("Groups", universityService.getGroups());
        model.addAttribute("Courses", universityService.getCourses());
        model.addAttribute("Students", universityService.getStudents());
        model.addAttribute("Teachers", universityService.getTeachers());
        return "admin/filling";
    }

    @GetMapping("/admin/filling/preset")
    public String fillPreset(Model model) throws PSQLException {
        universityService.removeAllEntities();
        universityService.fillUniversityEntities(countOfClassrooms,
                countOfGroups,
                countOfCourses,
                countOfStudents,
                countOfTeachers);
        model.addAttribute("message", "Университет обновлен!");
        model.addAttribute("Classrooms", universityService.getClassrooms());
        model.addAttribute("Groups", universityService.getGroups());
        model.addAttribute("Courses", universityService.getCourses());
        model.addAttribute("Students", universityService.getStudents());
        model.addAttribute("Teachers", universityService.getTeachers());
        return "admin/filling";
    }

    @PostMapping("/admin/filling")
    public String refreshUniversity(@RequestParam Map<String, String> form) throws PSQLException {

        int countOfClassrooms = Integer.parseInt(form.get("countOfClassrooms"));
        int countOfGroups = Integer.parseInt(form.get("countOfGroups"));
        int countOfCourses = Integer.parseInt(form.get("countOfCourses"));
        int countOfStudents = Integer.parseInt(form.get("countOfStudents"));
        int countOfTeachers = Integer.parseInt(form.get("countOfTeachers"));

        universityService.removeAllEntities();
        universityService.fillUniversityEntities(countOfClassrooms,
                countOfGroups,
                countOfCourses,
                countOfStudents,
                countOfTeachers);

        return "redirect:/admin/filling?message=ok";
    }


    @GetMapping("/admin/createSchedule")
    public String createSchedule() throws InterruptedException, PSQLException {
        scheduleService.deleteSchedule();
        try{
            scheduleService.createRandomSchedule();
        }catch (RuntimeException ex){
            scheduleService.deleteSchedule();
            return "redirect:/admin?error=true";
        }
        return "redirect:/schedule";
    }


    @GetMapping("/admin/removeSchedule")
    public String removeSchedule(){
        scheduleService.deleteSchedule();
        return "redirect:/admin";
    }


    @GetMapping("/admin/editSchedule")
    public String editSchedule(Model model,
                               @RequestParam(name = "day", required = false) String day,
                               @RequestParam(name = "classroom", required = false) Integer classroom,
                               @RequestParam(name = "lesson", required = false) Integer lesson){
        if(day != null){
            scheduleService.removeScheduleItem(day, classroom, lesson);
            return "redirect:/admin/editSchedule";
        }
        int countOfLessons = 0;
        List<List<ScheduleItem>> [] schedule = new ArrayList[6];
        for (int i = 0; i < 6; i++) {
            schedule[i] = scheduleService.getDaySchedule(DayOfWeek.of(i+1).name());
            for (List<ScheduleItem> classroomSchedule : schedule[i]) {
                countOfLessons += classroomSchedule.size();
            }
        }

        model.addAttribute("schedule", schedule);
        model.addAttribute("countOfLessons", countOfLessons);
        model.addAttribute("timeIntervals", Lessons.values());
        return "admin/scheduleAdmin";
    }


    @GetMapping("/admin/addLesson")
    public String getAddLesson(@RequestParam(name = "name", required = false, defaultValue = "Добавление нового урока") String name,
                               @RequestParam(name = "day") String day,
                               @RequestParam(name = "classroom") Integer classroom,
                               @RequestParam(name = "error", required = false) Boolean error,
                               Model model) {
        if(Boolean.TRUE.equals(error)){
            model.addAttribute("message", "Impossible to create this lesson!  Group or teacher are not available at this time.");
        }
        model.addAttribute("title", name);
        model.addAttribute("day", day);
        model.addAttribute("classroom", classroom);
        Set<Integer> lesNums = new HashSet<>();
        int[] lessons = scheduleService.getDaySchedule(day).get(classroom - 1).stream().mapToInt(ScheduleItem::getLessonNumber).toArray();
        for (int i = 1; i <= 6; i++) {
            lesNums.add(i);
            for (int num : lessons) {
                if(num == i){
                    lesNums.remove(i);
                    break;
                }
            }
        }
        model.addAttribute("title", name);
        model.addAttribute("numbersOfLessons", lesNums);
        model.addAttribute("groups", universityService.getGroups());
        model.addAttribute("teachers", universityService.getTeachers());
        model.addAttribute("courses", universityService.getCourses());
        return "admin/addLesson";
    }


    @PostMapping("/admin/addLesson")
    public String addLesson(@RequestParam(name = "name", required = false, defaultValue = "Добавление урока") String name,
                            @RequestParam(name = "day") String day,
                            @RequestParam(name = "classroom") Integer classroom,
                            @RequestParam(name = "lesson") Integer lesson,
                            @RequestParam(name = "course") Integer course,
                            @RequestParam(name = "group") Integer group,
                            @RequestParam(name = "teacher") Integer teacher,
                            Model model) {
        model.addAttribute("title", name);
        Integer resultId = scheduleService.addScheduleItem(new ScheduleItem(day, lesson, universityService.getClassroomById(classroom), universityService.getCourseById(course), universityService.getGroupById(group), universityService.getTeacherById(teacher)));
        if(resultId == 0){
            return "redirect:/admin/addLesson?day=" + day + "&classroom=" + classroom + "&error=true";
        }
        return "redirect:/admin/editSchedule";
    }

}
