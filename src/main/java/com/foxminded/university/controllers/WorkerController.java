package com.foxminded.university.controllers;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Lessons;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/classrooms")
@RequiredArgsConstructor
//@PreAuthorize("hasAuthority('WORKER')")
public class WorkerController {

    private final WorkerService workerService;
    private final UniversityService universityService;

    @GetMapping
    public String getClassrooms(Model model){
        model.addAttribute("classrooms", universityService.getClassrooms());
        return "classrooms";
    }

    @GetMapping("{classroomId}")
    public String schedule(@PathVariable Integer classroomId, Model model){
        Classroom classroom = universityService.getClassroomById(classroomId);
        model.addAttribute("classroom", classroom);
        model.addAttribute("timeIntervals", Lessons.values());
        model.addAttribute("schedule", workerService.getClassroomWeekSchedule(classroom));
        return "classroomSchedule";
    }

    @GetMapping("/all")
    public String allSchedule(Model model){
        model.addAttribute("schedule", workerService.getWeekSchedule());
        model.addAttribute("classrooms", universityService.getClassrooms());
        model.addAttribute("timeIntervals", Lessons.values());
        return "workerSchedule";
    }
}
