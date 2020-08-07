package com.foxminded.university.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;

@Schema(hidden = true)
public enum Role implements GrantedAuthority {
    USER, ADMIN, WORKER;

    @Override
    public String getAuthority() {
        return name();
    }
}
