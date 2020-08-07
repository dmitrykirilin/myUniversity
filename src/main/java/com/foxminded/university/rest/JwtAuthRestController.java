package com.foxminded.university.rest;

import com.foxminded.university.config.JwtTokenUtil;
import com.foxminded.university.model.User;
import com.foxminded.university.model.dto.JwtRequest;
import com.foxminded.university.services.JwtUserDetailsService;
import com.foxminded.university.util.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/rest/v1")
@RequiredArgsConstructor
@Tag(name = "Authorization API", description = "Registration and authorization")
public class JwtAuthRestController {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;

    @PostMapping(value = "/registration")
    @Operation(summary = "Registration of new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Data not found!",
                    content = @Content) })
    public ResponseEntity<Object> addNewUser(@Parameter(description="New users' credentials",
                                            required=true, schema=@Schema(implementation = JwtRequest.class))
                                            @RequestBody @Valid JwtRequest user,
                                             BindingResult bindingResult) throws PSQLException {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean isExists = jwtUserDetailsService.findByUsername(user.getUsername()) != null;
        if(isExists){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Identity constraint", "This username is already in use!");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        User userToStorage = new User(user.getUsername(), user.getPassword());
        jwtUserDetailsService.add(userToStorage);
        return new ResponseEntity<>(jwtUserDetailsService.findByUsername(user.getUsername()), HttpStatus.CREATED);
    }


    @PostMapping(value = "/authenticate")
    @Operation(summary = "Authorizing in application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authorization",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Impossible credentials",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User with this credentials is not founded",
                    content = @Content) })
    public ResponseEntity<?> createAuthenticationToken(@Parameter(description="Enter your credentials",
                                                        required=true, schema=@Schema(implementation = JwtRequest.class))
                                                           @RequestBody @Valid JwtRequest authenticationRequest,
                                                       BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
