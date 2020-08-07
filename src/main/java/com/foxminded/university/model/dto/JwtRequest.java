package com.foxminded.university.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(hidden = true)
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    @Size(min = 4, max = 20, message = "Придумайте никнейм от 4 до 20 символов!")
    @Schema(description = "Name of user", example = "admin")
    private String username;

    @Schema(description = "User' password", example = "admin")
    @Size(min = 4, message = "Пароль должен быть от 4 до 10 символов!")
    private String password;

}
