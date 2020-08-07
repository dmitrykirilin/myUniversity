package com.foxminded.university.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxminded.university.model.*;
import com.foxminded.university.model.dto.StudentDto;
import com.foxminded.university.repository.UserDAO;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.services.JwtUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UniversityService universityService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private JwtUserDetailsService userService;
    @Autowired
    private UserDAO userDAO;


    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnUserListTest() throws Exception {
        this.mockMvc.perform(get("/rest/v1/admin/users")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(userDAO.findAll())));
    }

    @Test
    public void shouldNotReturnUserListWithoutRootTest() throws Exception {
        this.mockMvc.perform(get("/rest/v1/admin/filling"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void postRequestShouldReturnCreatedStatusTest() throws Exception {
        Teacher teacher = new Teacher("Dmitry", "Kirilin");
        String toJson = mapToJson(teacher);
        universityService.fillUniversityEntities(0, 2, 0, 1, 1);
        this.mockMvc.perform(post("/rest/v1/admin/teachers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson))
                .andExpect(authenticated())
                .andExpect(status().isCreated());
        assertEquals(2, universityService.getTeachers().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @JsonView({Views.IdName.class})
    public void putRequestShouldReturnUpdateEntityTest() throws Exception {
        Student student = new Student("Dmitry", "Kirilin");
        StudentDto studentDto = new StudentDto(student, 1);
        String toJson = mapToJson(studentDto);
        universityService.fillUniversityEntities(0, 2, 0, 1, 1);
        this.mockMvc.perform(put("/rest/v1/admin/students/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson))
                .andExpect(authenticated())
                .andExpect(status().isOk());
        assertEquals(1, universityService.getStudents().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @JsonView({Views.IdName.class})
    public void deleteRequestShouldRemoveEntityTest() throws Exception {
        universityService.fillUniversityEntities(0, 2, 2, 1, 1);
        this.mockMvc.perform(delete("/rest/v1/admin/courses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string("Course and binding schedule are removed!"));
        assertEquals(1, universityService.getCourses().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @JsonView({Views.IdName.class})
    public void deleteRequestShouldRemovePersonTest() throws Exception {
        universityService.fillUniversityEntities(0, 2, 0, 1, 2);
        this.mockMvc.perform(delete("/rest/v1/admin/teachers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string("Teacher and binding schedule are removed!"));
        assertEquals(1, universityService.getTeachers().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnUserForEditTest() throws Exception {
        User user = userDAO.findByUsername("user");
        this.mockMvc.perform(get("/rest/v1/admin/users/{user}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(new HashMap<String, Object>(){{
                    put("user", user);
                    put("roles", Role.values());
                }})));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldRemoveUserByIdTest() throws Exception {
        User user = userDAO.findByUsername("user");
        this.mockMvc.perform(delete("/rest/v1/admin/users/{user}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("User is removed successfully!"));
        assertNull(userDAO.findByUsername(user.getUsername()));
        userDAO.save(new User("user", "user", Collections.singleton(Role.USER)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnPageWithUniversityFillingTest() throws Exception {
        this.mockMvc.perform(get("/admin/filling"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().attribute("Courses", universityService.getCourses()))
                .andExpect(model().size(5))
                .andExpect(view().name("admin/filling"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldAddLessonByGivenParamsTest() throws Exception {
        universityService.fillUniversityEntities(2, 2, 2, 5, 2);
        this.mockMvc.perform(post("/admin/addLesson").param("day", "MONDAY")
                .param("classroom", "1")
                .param("lesson", "1")
                .param("course", "1")
                .param("group", "1")
                .param("teacher", "1")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/editSchedule"));
        scheduleService.removeScheduleItem("MONDAY", 1, 1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnStudentEditPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/student"))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().attribute("groups", universityService.getGroups()))
                .andExpect(model().size(5))
                .andExpect(view().name("admin/editPerson"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnCoursesEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/courses"))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", universityService.getCourses()))
                .andExpect(model().size(3))
                .andExpect(view().name("admin/entities"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldNotAddNewGroupWithInvalidNameAndReturnGroupList() throws Exception {
        this.mockMvc.perform(post("/admin/addgroups")
                .param("name", "group_1")
                .param("id", "")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(view().name("admin/entities"))
                .andExpect(content().string(containsString("Имя не соответствует заданному шаблону!")));;
        assertEquals(0, universityService.getGroups().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldAddNewGroupWithValidNameAndReturnGroupList() throws Exception {
        this.mockMvc.perform(post("/admin/addgroups")
                .param("name", "aA-11")
                .param("id", "")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/groups"));
        assertEquals(1, universityService.getGroups().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldReturnErrorWhenTrySaveSameCourseNameTest() throws Exception {
        universityService.addCourse(new Course("chemistry", 5));
        this.mockMvc.perform(post("/admin/addcourses")
                .param("name", "chemistry")
                .param("hoursPerWeek", "5")
                .param("id", "")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/entities"))
                .andExpect(model().attribute("pageName", "courses"))
                .andExpect(content().string(containsString("Такой курс уже существует!")));
        assertEquals(1, universityService.getCourses().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldAddNewTeacherWithoutLastNameTest() throws Exception {
        this.mockMvc.perform(post("/admin/addteacher")
                .param("firstName", "Dmitry")
                .param("lastName", "")
                .param("id", "")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/editPerson"))
                .andExpect(model().size(4))
                .andExpect(content().string(containsString("Фамилия не может быть пустой!")));
        assertEquals(0, universityService.getTeachers().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void shouldEditExistsUserAndReturnUserListTest() throws Exception {
        User user = userService.findAll().get(0);
        int expectedSize = userService.findAll().size();
        this.mockMvc.perform(post("/users")
                .param("username", user.getUsername() + "Update")
                .param("userId", String.valueOf(user.getId()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        assertEquals(expectedSize, userService.findAll().size());
        assertEquals(userDAO.findByUsername(user.getUsername() + "Update"), userDAO.findById(user.getId()).get());
    }
}
