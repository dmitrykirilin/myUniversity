package com.foxminded.university.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Views;
import com.foxminded.university.services.UniversityService;
import com.foxminded.university.services.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@Tag(name = "Worker classroom observer", description = "Reviewing free classrooms")
public class WorkerRestController {

    private final UniversityService universityService;
    private final WorkerService workerService;

    @GetMapping(value = "/classrooms")
    @Operation(summary = "Receiving all classrooms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All classrooms was returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Classroom.class)) }),
            @ApiResponse(responseCode = "404", description = "Classrooms are not created!", content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Classroom>> getClassrooms() {
        List<Classroom> classrooms = universityService.getClassrooms();
        return new ResponseEntity<>(classrooms, HttpStatus.OK);
    }


    @GetMapping(value = "/classrooms/schedule")
    @Operation(summary = "Receiving all free intervals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All free intervals are received",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }),
            @ApiResponse(responseCode = "404", description = "Classrooms are not created!", content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<ScheduleItem>[]> getClassroomsSchedule() {
        List<ScheduleItem>[] weekSchedule = workerService.getWeekSchedule();
        return ResponseEntity.ok(weekSchedule);
    }


    @GetMapping(value = "/classrooms/{id}")
    @Operation(summary = "Receiving all free intervals for one auditorium")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Free intervals for one auditorium are recieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }),
            @ApiResponse(responseCode = "404", description = "Auditorium with such id is not exists!", content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<ScheduleItem>[]> getClassroomWeekSchedule(@Parameter(description="Classrooms' id for getting. Cannot be null or empty.",
                                                                        required=true, schema=@Schema(name = "id", implementation = String.class))
                                                                        @PathVariable("id") Classroom classroom) {
        if (classroom == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ScheduleItem>[] classroomWeekSchedule = workerService.getClassroomWeekSchedule(classroom);
        return ResponseEntity.ok(classroomWeekSchedule);
    }

}
