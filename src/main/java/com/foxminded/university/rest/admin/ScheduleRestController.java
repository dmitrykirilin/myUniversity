package com.foxminded.university.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Views;
import com.foxminded.university.model.dto.RequestUniversityEntitiesDto;
import com.foxminded.university.model.dto.ScheduleItemDto;
import com.foxminded.university.model.dto.UniversityEntities;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
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
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("rest/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@Tag(name = "Schedule and university routing", description = "Admin schedule API")
public class ScheduleRestController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;


    @GetMapping(value = "filling")
    @Operation(summary = "Receiving all entities of the university")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return all entities of the university",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UniversityEntities.class)) }),
            @ApiResponse(responseCode = "404", description = "Entities are not exists!", content = @Content) })
    @JsonView({Views.IdName.class})
    public ResponseEntity<UniversityEntities> getUniversityFilling(){
        UniversityEntities universityEntities = new UniversityEntities();
        universityEntities.setClassrooms(universityService.getClassrooms());
        universityEntities.setGroups(universityService.getGroups());
        universityEntities.setCourses(universityService.getCourses());
        universityEntities.setStudents(universityService.getStudents());
        universityEntities.setTeachers(universityService.getTeachers());
        return new ResponseEntity<>(universityEntities, HttpStatus.OK);
    }


    @PutMapping(value = "filling")
    @Operation(summary = "Creating new university entities from provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "University was refreshed",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UniversityEntities.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.IdName.class})
    public ResponseEntity<UniversityEntities> fillingUniversity(@Parameter(description="Student id for adding. Cannot be null or empty.",
                                                                required=true, schema=@Schema(name = "id", implementation = RequestUniversityEntitiesDto.class))
                                                                @RequestBody RequestUniversityEntitiesDto values) throws PSQLException {
        if(values.getCountOfClassrooms() == null ||
            values.getCountOfGroups() == null ||
            values.getCountOfCourses() == null ||
            values.getCountOfStudents() == null ||
            values.getCountOfTeachers() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        int countOfClassrooms = values.getCountOfClassrooms();
        int countOfGroups = values.getCountOfGroups();
        int countOfCourses = values.getCountOfCourses();
        int countOfStudents = values.getCountOfStudents();
        int countOfTeachers = values.getCountOfTeachers();
        universityService.removeAllEntities();
        universityService.fillUniversityEntities(countOfClassrooms,
                countOfGroups,
                countOfCourses,
                countOfStudents,
                countOfTeachers);
        UniversityEntities universityEntities = new UniversityEntities();
        universityEntities.setClassrooms(universityService.getClassrooms());
        universityEntities.setGroups(universityService.getGroups());
        universityEntities.setCourses(universityService.getCourses());
        universityEntities.setStudents(universityService.getStudents());
        universityEntities.setTeachers(universityService.getTeachers());
        return ResponseEntity.ok(universityEntities);
    }


    @GetMapping(value = "schedule")
    @Operation(summary = "Receiving full university schedule for week.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returning full schedule",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) })})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<List<ScheduleItem>> []> getFullSchedule(){
        List<List<ScheduleItem>> [] schedule = new ArrayList[6];
        for (int i = 0; i < 6; i++) {
            schedule[i] = scheduleService.getDaySchedule(DayOfWeek.of(i+1).name());
        }
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }


    @GetMapping(value = "schedule/{id}")
    @Operation(summary = "Receiving one lesson.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returning lesson by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }),
            @ApiResponse(responseCode = "404", description = "Lesson with this id is not found", content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<ScheduleItem> getScheduleItem(@Parameter(description="Lessons' id for getting. Cannot be null or empty.",
                                                        required=true, schema=@Schema(name = "id", implementation = String.class))
                                                        @PathVariable(name = "id") ScheduleItem scheduleItem){
        if (scheduleItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(scheduleItem);
    }


    @GetMapping(value = "schedule/new")
    @Operation(summary = "Creating new schedule for university.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return full schedule",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleItem.class)) }),
            @ApiResponse(responseCode = "409", description = "Impossible to create new schedule for this amount of entities, try to reduce amount of courses or groups.", content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<List<ScheduleItem>> []> getNewScheduleFilling(){
        scheduleService.deleteSchedule();
        try{
            scheduleService.createRandomSchedule();
        }catch (RuntimeException ex){
            scheduleService.deleteSchedule();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        List<List<ScheduleItem>> [] schedule = new ArrayList[6];
        for (int i = 0; i < 6; i++) {
            schedule[i] = scheduleService.getDaySchedule(DayOfWeek.of(i+1).name());
        }
        return ResponseEntity.ok(schedule);
    }


    @DeleteMapping(value = "schedule/{id}")
    @Operation(summary = "Removing lesson by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson was removed successfully!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "This lesson does not exists", content = @Content) })
    public ResponseEntity<?> deleteScheduleItem(@Parameter(description="Lessons' id for removing. Cannot be null or empty.",
                                                required=true, schema=@Schema(name = "id", implementation = String.class))
                                                @PathVariable("id") ScheduleItem scheduleItem) {
        if (scheduleItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer id = scheduleItem.getId();
        scheduleService.removeScheduleItem(id);
        return ResponseEntity.ok("Lesson was removed successfully!");
    }


    @PostMapping(value = "schedule")
    @Operation(summary = "Adding new lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lesson created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<ScheduleItem> addNewScheduleItem(@Parameter(description="Lesson data for adding to schedule. Cannot be null or empty.",
                                                            required=true, schema=@Schema(implementation = ScheduleItemDto.class))
                                                            @RequestBody ScheduleItemDto scheduleItemDto) {
        if (scheduleItemDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Integer resultId = scheduleService.addScheduleItem(new ScheduleItem(scheduleItemDto.getDayOfWeek(),
                                                                            scheduleItemDto.getLessonNumber(),
                                                                            universityService.getClassroomById(scheduleItemDto.getClassroom()),
                                                                            universityService.getCourseById(scheduleItemDto.getCourse()),
                                                                            universityService.getGroupById(scheduleItemDto.getGroup()),
                                                                            universityService.getTeacherById(scheduleItemDto.getTeacher())));
        return new ResponseEntity<>(scheduleService.getScheduleItemById(resultId), HttpStatus.CREATED);
    }
}
