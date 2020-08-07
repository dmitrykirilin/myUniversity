package com.foxminded.university.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Views;
import com.foxminded.university.model.dto.StudentDto;
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
@Tag(name = "Teachers", description = "Teacher API")
public class TeacherRestController {

    private final UniversityService universityService;
    private final ScheduleService scheduleService;

    @GetMapping(value = "teachers")
    @Operation(summary = "Get all teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all of teachers",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Teacher.class)) })})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = universityService.getTeachers();
        return ResponseEntity.ok(teachers);
    }


    @DeleteMapping(value = "teachers/{id}")
    @Operation(summary = "Delete student by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher and binding schedule are removed!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content) })
    public ResponseEntity<?> deleteTeacher(@Parameter(description="Teacher for adding. Cannot be null or empty.",
                                            required=true, schema=@Schema(implementation = String.class))
                                            @PathVariable("id") Teacher teacher) {
        if(teacher == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer id = teacher.getId();
        universityService.removeTeacherById(id);
        scheduleService.removeAllItemsByTeacherId(id);
        return ResponseEntity.ok("Teacher and binding schedule are removed!");
    }


    @PostMapping(value = "teachers")
    @Operation(summary = "Adding new teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teacher created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> addNewTeacher(@Parameter(description="id of teacher to be deleted",
                                                required=true, schema=@Schema(implementation = Teacher.class))
                                                @RequestBody @Valid Teacher teacher,
                                                BindingResult bindingResult) throws PSQLException {
        if (teacher == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Teacher storageTeacher = universityService.findTeacherByFullName(teacher.getFirstName(), teacher.getLastName());
        boolean isExists = storageTeacher != null;
        if(isExists){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Identity constraint", "This teacher is already exists!");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        teacher.setFaculty(universityService.getFaculties().get(0));
        Integer id = universityService.addTeacher(teacher);
        teacher.setId(id);
        return new ResponseEntity<>(teacher, HttpStatus.CREATED);
    }


    @PutMapping(value = "teachers/{id}")
    @Operation(summary = "Updating teacher by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher is updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid data",
                    content = @Content) })
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> updateTeacher(@Parameter(description = "id of teacher to be updated",
                                                schema = @Schema(name = "id", implementation = String.class))
                                                @PathVariable(name = "id") Teacher teacherFromDB,
                                                @RequestBody @Valid Teacher teacher,
                                                BindingResult bindingResult) throws PSQLException {
        if (teacherFromDB == null || teacher == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(teacher, teacherFromDB, "id", "faculty");
        universityService.addTeacher(teacherFromDB);
        return ResponseEntity.ok(teacher);
    }
}
