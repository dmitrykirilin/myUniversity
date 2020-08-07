package com.foxminded.university.controllers;

import com.foxminded.university.model.Lessons;
import com.foxminded.university.model.User;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.StudentService;
import com.foxminded.university.services.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ScheduleService scheduleService;
    private final UniversityService universityService;
    private final StudentService studentService;

    public Map<Integer, String> colorSchemas = new LinkedHashMap<Integer, String>(){{
        put(1, "bg-primary mb-3");
        put(2, "bg-secondary mb-3");
        put(3, "bg-success mb-3");
        put(4, "bg-danger mb-3");
        put(5, "bg-warning mb-3");
        put(6, "bg-info mb-3");
    }};


    @GetMapping("/")
    public String homePage(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "name", required = false, defaultValue = "Главная страница") String name, Model model) {

        String dayOfWeek = LocalDate.now().getDayOfWeek().name();
        model.addAttribute("title", name);
        model.addAttribute("day", dayOfWeek);
        model.addAttribute("schedule", scheduleService.getDaySchedule(dayOfWeek));
        model.addAttribute("courses", universityService.getCourses());
        model.addAttribute("groups", universityService.getGroups());
        model.addAttribute("teachers", universityService.getTeachers());
        model.addAttribute("classrooms", universityService.getClassrooms());
        model.addAttribute("colorMap", colorSchemas);
        model.addAttribute("timeIntervals", Lessons.values());
        return "home";
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "name", required = false, defaultValue = "Главная страница") String name, Model model) {
        model.addAttribute("title", name);
        return "redirect:/";
    }

    @GetMapping("/teachers")
    public String getTeachers(Model model){
        model.addAttribute("teachers", universityService.getTeachers());
        return "teachers";
    }

    @GetMapping("/groups")
    public String getGroups(Model model){
        model.addAttribute("groups", universityService.getGroups());
        model.addAttribute("students", studentService.getStudentsByGroup(1));
        return "groups";
    }
}
