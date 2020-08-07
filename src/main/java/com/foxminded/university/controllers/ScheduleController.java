package com.foxminded.university.controllers;

import com.foxminded.university.model.Group;
import com.foxminded.university.model.Lessons;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.StudentService;
import com.foxminded.university.services.TeacherService;
import com.foxminded.university.services.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final StudentService studentService;
    private final UniversityService universityService;
    private final TeacherService teacherService;

    public Map<Integer, String> colorSchemas = new LinkedHashMap<Integer, String>(){{
        put(1, "bg-primary mb-3");
        put(2, "bg-secondary mb-3");
        put(3, "bg-success mb-3");
        put(4, "bg-danger mb-3");
        put(5, "bg-warning mb-3");
        put(6, "bg-info mb-3");
    }};

    @GetMapping("/schedule")
    public String allSchedule(@RequestParam(name = "name", required = false, defaultValue = "Расписание") String name,
                              Model model) {
        model.addAttribute("title", name);
        List<List<ScheduleItem>> [] schedule = new ArrayList[6];
        for (int i = 0; i < 6; i++) {
            schedule[i] = scheduleService.getDaySchedule(DayOfWeek.of(i+1).name());
        }
        model.addAttribute("courses", universityService.getCourses());
        model.addAttribute("classrooms", universityService.getClassrooms());
        model.addAttribute("groups", universityService.getGroups());
        model.addAttribute("teachers", universityService.getTeachers());
        model.addAttribute("schedule", schedule);
        model.addAttribute("colorMap", colorSchemas);
        model.addAttribute("timeIntervals", Lessons.values());
        return "schedule";
    }


    @GetMapping("/teacher/{id}")
    public String getTeacherSchedule(@PathVariable(value = "id") Integer id,
                                     Model model){
        Teacher teacher = universityService.getTeacherById(id);
        model.addAttribute("schedule", teacherService.getWeekSchedule(teacher));
        model.addAttribute("teacher", teacher);
        model.addAttribute("colorMap", colorSchemas);
        model.addAttribute("timeIntervals", Lessons.values());
        return "teacherSchedule";
    }

    @GetMapping("/group/{id}")
    public String getGroupSchedule(@PathVariable(value = "id") Integer id,
                                     Model model){
        Group group = universityService.getGroupById(id);
        model.addAttribute("schedule", studentService.getWeekSchedule(group));
        model.addAttribute("group", group);
        model.addAttribute("colorMap", colorSchemas);
        model.addAttribute("timeIntervals", Lessons.values());
        return "groupSchedule";
    }
}
