package com.foxminded.university.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxminded.university.model.User;
import com.foxminded.university.services.JwtUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource("/application.properties")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUserDetailsService userService;

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void tryCorrectLoginTest() throws Exception {
        String toJson = mapToJson(new HashMap<String, String>(){{
            put("username", "admin");
            put("password", "admin");
        }});
        this.mockMvc.perform(post("/rest/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson))
                .andExpect(status().isOk());
    }

    @Test
    public void tryWrongCredentialsTest() throws Exception {
        String toJson = mapToJson(new HashMap<String, String>(){{
            put("username", "admin");
            put("password", "nimda");
        }});
        this.mockMvc.perform(post("/rest/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void tryRegisterNewUserTest() throws Exception {
        String toJson = mapToJson(new HashMap<String, String>(){{
            put("username", "newUser");
            put("password", "nimda");
        }});
        this.mockMvc.perform(post("/rest/v1/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapToJson(userService.findByUsername("newUser"))));
    }

    @Test
    public void tryToGetRegPageWithoutRootTest() throws Exception {
        this.mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("title", "Регистрация"))
                .andExpect(view().name("registration"))
                .andExpect(model().size(2));
    }

//    @Test
//    @WithUserDetails(value = "admin")
//    public void tryToGetRegPageWithRootTest() throws Exception {
//        this.mockMvc.perform(get("/registration"))
//                .andExpect(authenticated())
//                .andExpect(status().is4xxClientError());
//    }

    @Test
    public void shouldReturnRegisterFormIfNameOccupiedTest() throws Exception {
        userService.add(new User("Dmitry", "12345"));
        this.mockMvc.perform(post("/registration")
                .param("username", "Dmitry")
                .param("password", "12345")
                .param("passwordConfirm", "12345")
                .param("id", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration"));
    }

    @Test
    public void shouldReturnRegisterFormIfPasswordNotConfirmTest() throws Exception {
        this.mockMvc.perform(post("/registration")
                .param("username", "Dmitry")
                .param("password", "12345")
                .param("passwordConfirm", "1234")
                .param("id", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(content().string(containsString("Подтвердите правильность ввода пароля!")));
    }


}
