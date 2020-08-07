package com.foxminded.university.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Views;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.StudentService;
import com.foxminded.university.services.TeacherService;
import com.foxminded.university.services.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/v1")
@RequiredArgsConstructor
@Tag(name = "Main university reviewing", description = "Observing schedule and entities")
public class MainUniversityRestController {

    private final ScheduleService scheduleService;
    private final StudentService studentService;
    private final UniversityService universityService;
    private final TeacherService teacherService;


    @GetMapping(value = "/")
    @Operation(summary = "Receiving today schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Today schedule is returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<List<ScheduleItem>>> getTodayShedule() {
        String dayOfWeek = LocalDate.now().getDayOfWeek().name();
        List<List<ScheduleItem>> daySchedule = scheduleService.getDaySchedule(dayOfWeek);
        return ResponseEntity.ok(daySchedule);
    }


    @GetMapping(value = "/teachers")
    @Operation(summary = "Receiving all university's teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All teachers are returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Teacher.class)) }),
            @ApiResponse(responseCode = "404", description = "Teachers are not exists!", content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = universityService.getTeachers();
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }


    @GetMapping(value = "/groups")
    @Operation(summary = "Receiving all university's groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All groups are returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class)) }),
            @ApiResponse(responseCode = "404", description = "Groups are not exists!", content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = universityService.getGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }


    @GetMapping(value = "/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Receiving full university's schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full schedule is returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<List<ScheduleItem>> []> allSchedule() {
        List<List<ScheduleItem>> [] schedule = new ArrayList[6];
        for (int i = 0; i < 6; i++) {
            schedule[i] = scheduleService.getDaySchedule(DayOfWeek.of(i+1).name());
        }
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }


    @GetMapping(value = "/teachers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Receiving one teacher by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher received",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Teacher.class)) }),
            @ApiResponse(responseCode = "404", description = "Teacher with this id is not found", content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<ScheduleItem>[]> getTeacherSchedule(@Parameter(description="Teacher's id for getting. Cannot be null or empty.",
                                                                    required=true, schema=@Schema(name = "id", implementation = String.class))
                                                                    @PathVariable("id") Teacher teacher) {
        if (teacher == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ScheduleItem>[] weekTeacherSchedule = teacherService.getWeekSchedule(teacher);
        return new ResponseEntity<>(weekTeacherSchedule, HttpStatus.OK);
    }


    @GetMapping(value = "/groups/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Receiving one group by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group received",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class)) }),
            @ApiResponse(responseCode = "404", description = "Group with this id is not found", content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<ScheduleItem>[]> getGroupSchedule(@Parameter(description="Group's id for getting. Cannot be null or empty.",
                                                                required=true, schema=@Schema(name = "id", implementation = String.class))
                                                                @PathVariable("id") Group group) {
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ScheduleItem>[] weekGroupSchedule = studentService.getWeekSchedule(group);
        return new ResponseEntity<>(weekGroupSchedule, HttpStatus.OK);
    }
}
