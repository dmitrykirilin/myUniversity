package com.foxminded.university.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxminded.university.model.Lessons;
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
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/University-and-drop.sql")
@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTest {

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
    void setUp() throws PSQLException {
        universityAgregator.build();
        scheduleService.createRandomSchedule();
    }

    @Test
    public void shouldGetAllScheduleTest() throws Exception {
        this.mockMvc.perform(get("/schedule"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("groups", universityService.getGroups()))
                .andExpect(view().name("schedule"))
                .andExpect(model().size(8));
    }

    @Test
    public void shouldGetTeacherScheduleByIdTest() throws Exception {
        Integer teacherId = 1;
        this.mockMvc.perform(get("/teacher/{id}", teacherId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("timeIntervals", Lessons.values()))
                .andExpect(model().attribute("teacher", universityService.getTeacherById(teacherId)))
                .andExpect(view().name("teacherSchedule"))
                .andExpect(model().size(4));
    }

    @Test
    public void shouldGetGroupScheduleByIdTest() throws Exception {
        Integer groupId = 1;
        this.mockMvc.perform(get("/group/{id}", groupId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("group", universityService.getGroupById(groupId)))
                .andExpect(view().name("groupSchedule"))
                .andExpect(model().size(4));
    }

    @Test
    public void shouldGetTeacherScheduleByIdInRestTest() throws Exception {
        Integer teacherId = 1;
        MvcResult mvcResult = this.mockMvc.perform(get("/rest/v1/teachers/{id}", teacherId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        ArrayList[] arrays = mapFromJson(content, ArrayList[].class);
        assertTrue(arrays.length > 0);
    }
}
