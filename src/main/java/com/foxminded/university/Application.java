package com.foxminded.university;

import com.foxminded.university.services.JwtUserDetailsService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "University application",
                                description = "Spring Boot RESTful service using OpenAPI 3"))
@SecurityScheme(name = "Authorization",
                scheme = "bearer",
                in = SecuritySchemeIn.HEADER, bearerFormat = "JWT",
                type = SecuritySchemeType.APIKEY)
public class Application implements ApplicationRunner {

    @Autowired
    private JwtUserDetailsService userService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {

        userService.addStartedUsers();

    }
}
