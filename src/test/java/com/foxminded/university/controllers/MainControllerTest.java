package com.foxminded.university.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.util.UniversityAgregator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/University-and-drop.sql")
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UniversityAgregator universityAgregator;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UniversityService universityService;

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    @BeforeEach
    public void setUp() throws PSQLException {
        universityAgregator.build();
        scheduleService.createRandomSchedule();
    }

    @Test
    public void shouldReceiveMainPageTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(LocalDate.now().getDayOfWeek().name())))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().size(9));
    }

    @Test
    public void redirectToMainPageTest() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void getTeacherPageTest() throws Exception {
        this.mockMvc.perform(get("/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("teachers", universityService.getTeachers()))
                .andExpect(view().name("teachers"))
                .andExpect(model().size(1));
    }

    @Test
    public void getGroupsPageTest() throws Exception {
        this.mockMvc.perform(get("/groups"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("groups", universityService.getGroups()))
                .andExpect(view().name("groups"))
                .andExpect(model().size(2));
    }

    @Test
    public void getTeachersInRestTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/rest/v1/teachers")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Teacher[] teachers = mapFromJson(content, Teacher[].class);
        assertTrue(teachers.length > 0);
    }
}