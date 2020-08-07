package com.foxminded.university.controllers.admin;

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.util.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequiredArgsConstructor
public class EntityController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;
    private final GroupValidator groupValidator;

    /*
    Teachers
     */

    @GetMapping("/admin/teacher")
    public String editTeachers(@ModelAttribute("person") Teacher teacher,
                                Model model,
                               @RequestParam(name = "name", required = false, defaultValue = "Редактор") String name){
        model.addAttribute("title", name);
        model.addAttribute("persons", universityService.getTeachers());
        model.addAttribute("item", "teacher");
        return "admin/editPerson";
    }

    @GetMapping("/admin/teacher/{id}/remove")
    public String removeTeacher(@PathVariable(value = "id") Integer id){

        universityService.removeTeacherById(id);
        scheduleService.removeAllItemsByTeacherId(id);
        return "redirect:/admin/teacher";
    }

    @GetMapping("/admin/teacher/{id}/edit")
    public String editTeacher(Model model,
                              @PathVariable(value = "id") Integer id,
                              @RequestParam(name = "name", required = false, defaultValue = "Редактор") String name){

        Teacher teacher = universityService.getTeacherById(id);
        model.addAttribute("person", teacher);
        model.addAttribute("personId", id);
        model.addAttribute("title", name);
        model.addAttribute("persons", universityService.getTeachers());
        model.addAttribute("item", "teacher");
        return "admin/editPerson";
    }

    @PostMapping("/admin/addteacher")
    public String addTeacher( @Valid @ModelAttribute("person") Teacher teacher,
                              BindingResult bindingResult,
                             Model model,
                             @RequestParam(name = "id") String id) throws PSQLException {

        Teacher storageTeacher = universityService.findTeacherByFullName(teacher.getFirstName(), teacher.getLastName());
        boolean isExists = storageTeacher != null;
        if(isExists){
            bindingResult.rejectValue("firstName", "error.name","Этот преподаватель уже зарегистрирован!");
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("persons", universityService.getTeachers());
            model.addAttribute("personId", teacher.getId());
            model.addAttribute("item", "teacher");
            return "admin/editPerson";
        }
        teacher.setFaculty(universityService.getFaculties().get(0));
        universityService.addTeacher(teacher);
        return "redirect:/admin/teacher";
    }

    /*
    Students
     */

    @GetMapping("/admin/student")
    public String editStudent(@ModelAttribute("person") Student student,
                                Model model,
                              @RequestParam(name = "name", required = false, defaultValue = "Редактор") String name){
        model.addAttribute("title", name);
        model.addAttribute("persons", universityService.getStudents());
        model.addAttribute("groups", universityService.getGroups());
        model.addAttribute("item", "student");
        return "admin/editPerson";
    }

    @GetMapping("/admin/student/{id}/remove")
    public String removeStudent(@PathVariable(value = "id") Integer id){

        universityService.removeStudentById(id);
        return "redirect:/admin/student";
    }

    @GetMapping("/admin/student/{id}/edit")
    public String editStudent(Model model,
                              @PathVariable(value = "id") Integer id,
                              @RequestParam(name = "name", required = false, defaultValue = "Редактор") String name){

        Student student = universityService.getStudentById(id);
        model.addAttribute("person", student);
//        model.addAttribute("firstName", student.getFirstName());
//        model.addAttribute("lastName", student.getLastName());
        model.addAttribute("personId", id);
        model.addAttribute("title", name);
        model.addAttribute("persons", universityService.getStudents());
        model.addAttribute("item", "student");
        model.addAttribute("groups", universityService.getGroups());
        return "admin/editPerson";
    }

    @PostMapping("/admin/addstudent")
    public String addStudent(@RequestParam(name = "group") String groupId,
                            @Valid @ModelAttribute("person") Student student,
                             BindingResult bindingResult,
                             Model model,
                             @RequestParam(name = "id") String id) throws PSQLException {

        Student storageStudent = universityService.findStudentByFullName(student.getFirstName(), student.getLastName());
        boolean isExists = storageStudent != null && universityService.getGroupById(Integer.valueOf(groupId)).equals(storageStudent.getGroup());
        if(isExists){
            bindingResult.rejectValue("firstName", "error.name","Данный студент уже зарегистрирован!");
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("persons", universityService.getStudents());
            model.addAttribute("groups", universityService.getGroups());
            model.addAttribute("personId", student.getId());
            model.addAttribute("item", "student");
            return "admin/editPerson";
        }
        student.setGroup(universityService.getGroupById(Integer.valueOf(groupId)));
        universityService.addStudent(student);
        return "redirect:/admin/student";
    }


    /*
    Courses
     */

    @GetMapping("/admin/courses")
    public String editCourses(@ModelAttribute("entity") Course course,
                            Model model){
        model.addAttribute("items", universityService.getCourses());
        model.addAttribute("pageName", "courses");
        return "admin/entities";
    }

    @GetMapping("/admin/courses/{id}/remove")
    public String removeCourse(@PathVariable(value = "id") Integer id){

        universityService.removeCourseById(id);
        scheduleService.removeAllItemsByCourseId(id);
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{id}/edit")
    public String editCourse(Model model,
                             @PathVariable(value = "id") Integer id,
                             @RequestParam(name = "pageName", required = false, defaultValue = "Редактор") String pageName){

        Course course = universityService.getCourseById(id);
        model.addAttribute("entity", course);
        model.addAttribute("name", course.getName());
        model.addAttribute("hoursPerWeek", course.getHoursPerWeek());
        model.addAttribute("itemId", id);
        model.addAttribute("title", pageName);
        model.addAttribute("items", universityService.getCourses());
        model.addAttribute("pageName", "courses");
        return "admin/entities";
    }

    @PostMapping("/admin/addcourses")
    public String addCourse(@RequestParam(name = "id") String id,
                            @Valid @ModelAttribute("entity") Course course,
                            BindingResult bindingResult,
                            Model model) throws PSQLException {

        Course storageCourse = universityService.findCourseByName(course.getName());
        boolean isExists = storageCourse != null && storageCourse.getHoursPerWeek().equals(course.getHoursPerWeek());
        if(isExists){
            bindingResult.rejectValue("name", "error.name","Такой курс уже существует!");
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("items", universityService.getCourses());
            model.addAttribute("pageName", "courses");
            model.addAttribute("itemId", course.getId());
            return "admin/entities";
        }
        universityService.addCourse(course);
        return "redirect:/admin/courses";
    }

    /*
    Groups
     */

    @GetMapping("/admin/groups")
    public String editGroups(Group group,
                            Model model){
        model.addAttribute("entity", group);
        model.addAttribute("items", universityService.getGroups());
        model.addAttribute("pageName", "groups");
        return "admin/entities";
    }

    @GetMapping("/admin/groups/{id}/remove")
    public String removeGroup(@PathVariable(value = "id") Integer id){

        universityService.removeGroupById(id);
        scheduleService.removeAllItemsByGroupId(id);
        return "redirect:/admin/groups";
    }

    @GetMapping("/admin/groups/{id}/edit")
    public String editGroup(Model model,
                            @PathVariable(value = "id") Integer id,
                            @RequestParam(name = "pageName", required = false, defaultValue = "Редактор") String pageName){

        Group group = universityService.getGroupById(id);
        model.addAttribute("entity", group);
        model.addAttribute("name", group.getName());
        model.addAttribute("itemId", id);
        model.addAttribute("title", pageName);
        model.addAttribute("items", universityService.getGroups());
        model.addAttribute("pageName", "groups");
        return "admin/entities";
    }


    @PostMapping("/admin/addgroups")
    public String addGroup(@Valid @ModelAttribute("entity") Group group,
                           BindingResult bindingResult,
                           Model model,
                           @RequestParam(name = "id") String id) throws PSQLException {

        groupValidator.validate(group, bindingResult);
        boolean isExists = universityService.findGroupByName(group.getName()) != null;
        if (isExists) {
            bindingResult.rejectValue("name", "error.name", "Группа с данным именем уже существует!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("items", universityService.getGroups());
            model.addAttribute("pageName", "groups");
            model.addAttribute("itemId", group.getId());
            return "admin/entities";
        }
        universityService.addGroup(group);
        return "redirect:/admin/groups";
    }
}
