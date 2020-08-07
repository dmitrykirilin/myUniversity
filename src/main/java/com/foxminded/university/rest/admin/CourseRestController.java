package com.foxminded.university.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Views;
import com.foxminded.university.services.ScheduleService;
import com.foxminded.university.services.UniversityService;
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
@Tag(name = "Course", description = "Course API")
public class CourseRestController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;


    @GetMapping(value = "courses")
    @Operation(summary = "Get all courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all of courses",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Course.class)) })})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = universityService.getCourses();
        return ResponseEntity.ok(courses);
    }


    @DeleteMapping(value = "courses/{id}")
    @Operation(summary = "Delete course by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course and binding schedule are removed!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content) })
    public ResponseEntity<?> deleteCourse(@Parameter(description = "id of course to be deleted", schema = @Schema(name = "id", implementation = String.class))
                                              @PathVariable("id") Course course) {
        if(course == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer id = course.getId();
        universityService.removeCourseById(id);
        scheduleService.removeAllItemsByCourseId(id);
        return ResponseEntity.ok("Course and binding schedule are removed!");
    }


    @PostMapping(value = "courses")
    @Operation(summary = "Adding new course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> addNewCourse(@Parameter(description="Course for adding. Cannot be null or empty.",
                                                required=true, schema=@Schema(implementation = Course.class))
                                                @RequestBody @Valid Course course,
                                               BindingResult bindingResult) throws PSQLException {
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Course storageCourse = universityService.findCourseByName(course.getName());
        boolean isExists = storageCourse != null && storageCourse.getHoursPerWeek().equals(course.getHoursPerWeek());
        if(isExists){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Identity constraint", "This course is already exists!");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        if(storageCourse != null){
            course.setId(storageCourse.getId());
        }
        Integer id = universityService.addCourse(course);
        course.setId(id);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }


    @PutMapping(value = "courses/{id}")
    @Operation(summary = "Updating course by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course is updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid data",
                    content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> updateCourse(@Parameter(description = "id of course to be updated",
                                                        schema = @Schema(name = "id", implementation = String.class))
                                                   @PathVariable(name = "id") Course courseFromDB,
                                               @Parameter(description="Course for adding. Cannot be null or empty.",
                                                       required=true, schema=@Schema(implementation = Course.class))
                                               @RequestBody @Valid Course course,
                                               BindingResult bindingResult) throws PSQLException {
        if (courseFromDB == null || course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(course, courseFromDB, "id");
        universityService.addCourse(courseFromDB);
        return ResponseEntity.ok(courseFromDB);
    }
}
