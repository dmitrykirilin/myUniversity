package com.foxminded.university.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Views;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.util.ControllerUtils;
import com.foxminded.university.util.GroupValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rest/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@Tag(name = "Group", description = "Group API")
public class GroupRestController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;
    private final GroupValidator groupValidator;

    @GetMapping(value = "groups")
    @Operation(summary = "Get all groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All of groups are received",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class)) })})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = universityService.getGroups();
        return ResponseEntity.ok(groups);
    }


    @DeleteMapping(value = "groups/{id}")
    @Operation(summary = "Delete group by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group and binding schedule are removed!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Group not found", content = @Content) })
    public ResponseEntity<?> deleteGroup(@Parameter(description = "id of group to be deleted", schema = @Schema(name = "id", implementation = String.class))
                                                 @PathVariable("id") Group group) {
        if(group == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer id = group.getId();
        universityService.removeGroupById(id);
        scheduleService.removeAllItemsByGroupId(id);
        return ResponseEntity.ok("Course and binding schedule are removed!");
    }


    @PostMapping(value = "groups")
    @Operation(summary = "Adding new group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> addNewGroup(@Parameter(description="Group for adding. Cannot be null or empty.",
                                                required=true, schema=@Schema(implementation = Group.class))
                                                @RequestBody @Valid Group group,
                                              BindingResult bindingResult) throws PSQLException {
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean isExists = universityService.findGroupByName(group.getName()) != null;
        if(isExists){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Identity constraint", "This group is already exists!");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        groupValidator.validate(group, bindingResult);
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Integer id = universityService.addGroup(group);
        group.setId(id);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }


    @PutMapping(value = "groups/{id}")
    @Operation(summary = "Updating group by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group is updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid data",
                    content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> updateGroup(@Parameter(description = "id of group to be updated",
                                              schema = @Schema(name = "id", implementation = String.class))
                                              @PathVariable(name = "id") Group groupFromDB,
                                              @Parameter(description="Group for adding. Cannot be null or empty.",
                                              required=true, schema=@Schema(implementation = Group.class))
                                              @RequestBody @Valid Group group,
                                              BindingResult bindingResult) throws PSQLException {
        if (groupFromDB == null || group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(group, groupFromDB, "id");
        universityService.addGroup(groupFromDB);
        return ResponseEntity.ok(groupFromDB);
    }
}
