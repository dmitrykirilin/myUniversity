package com.foxminded.university.rest.admin;

import com.foxminded.university.model.Role;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.User;
import com.foxminded.university.model.dto.StudentDto;
import com.foxminded.university.model.dto.UserDto;
import com.foxminded.university.repository.UserDAO;
import com.foxminded.university.util.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("rest/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@Tag(name = "Users", description = "Users' entities API")
public class UserRestController {

    private final UserDAO userDAO;


    @GetMapping(value = "users")
    @Operation(summary = "Receiving all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all of users",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) })})
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userDAO.findAll(), HttpStatus.OK);
    }


    @GetMapping(value = "users/{user}")
    @Operation(summary = "Receiving one user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully getting user by id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User with this id not founded", content = @Content) })
    public ResponseEntity<Object> getUser(@Parameter(description="User id for getting. Cannot be null or empty.",
                                            required=true, schema=@Schema(name = "id", implementation = String.class))
                                            @PathVariable User user){
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HashMap<String, Object> response = new HashMap<String, Object>(){{
            put("user", user);
            put("roles", Role.values());
        }};
        return ResponseEntity.ok(response);
    }


    @DeleteMapping(value = "users/{user}")
    @Operation(summary = "Delete user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was removed!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User with recieved id is not founded", content = @Content) })
    public ResponseEntity<?> deleteUser(@Parameter(description="User id for removing. Cannot be null or empty.",
                                        required=true, schema=@Schema(name = "id", implementation = String.class))
                                        @PathVariable("user") User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userDAO.delete(user);
        return ResponseEntity.ok("User is removed successfully!");
    }


    @PutMapping(value = "users/{id}")
    @Operation(summary = "Refreshing users' data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid data",
                    content = @Content) })
    public ResponseEntity<Object> updateUser(@Parameter(description = "id of user to be updating",
                                             schema = @Schema(name = "id", implementation = String.class))
                                             @PathVariable(name = "id") User userFromDB,
                                             @Parameter(description="User for adding. Cannot be null or empty.",
                                             required=true, schema=@Schema(implementation = UserDto.class))
                                             @RequestBody @Valid UserDto userDto,
                                             BindingResult bindingResult) throws PSQLException {
        if (userFromDB == null || userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        if(userFromDB.getRoles() != null) {
            userFromDB.getRoles().clear();
        }
        userFromDB.setUsername(userDto.getUser().getUsername());
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        userFromDB.setRoles(new HashSet<>());
        for (String role : userDto.getRoles()) {
            if(roles.contains(role)){
                userFromDB.getRoles().add(Role.valueOf(role));
            }
        }
        userDAO.save(userFromDB);
        return new ResponseEntity<>(userFromDB, HttpStatus.CREATED);
    }
}
