package com.foxminded.university.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Getter @Setter
@NoArgsConstructor @RequiredArgsConstructor
@EqualsAndHashCode(of = {"username"})
@ToString(of = {"username"})
@Table(name = "users")
@Tag(name = "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the user.")
    private long id;

    @NonNull
    @Size(min = 4, max = 20, message = "Придумайте никнейм от 4 до 20 символов!")
    @Schema(example = "User", description = "Username")
    private String username;

    @NonNull
    @Size(min = 4, message = "Пароль должен быть от 4 до 10 символов!")
    @JsonIgnore
    @Schema(hidden = true)
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Schema(hidden = true)
    private Set<Role> roles;

    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }


    @Schema(hidden = true)
    public boolean isAdmin(){
        return roles.contains(Role.ADMIN);
    }

    @Schema(hidden = true)
    public boolean isWorker(){
        return roles.contains(Role.WORKER);
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isEnabled() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Schema(hidden = true)
    public String getRolesInString(){
        StringJoiner sj = new StringJoiner(", ");
        for (Role role : getRoles()) {
            sj.add(role.name());
        }
        return sj.toString();
    }
}
