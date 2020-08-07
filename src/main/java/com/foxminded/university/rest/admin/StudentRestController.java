package com.foxminded.university.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Views;
import com.foxminded.university.model.dto.StudentDto;
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
@Tag(name = "Students", description = "Student API")
public class StudentRestController {

    private final UniversityService universityService;

    @GetMapping(value = "students")
    @Operation(summary = "Get all students")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all of students",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)) })})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = universityService.getStudents();
        return ResponseEntity.ok(students);
    }


    @DeleteMapping(value = "students/{id}")
    @Operation(summary = "Delete student by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students and binding schedule are removed!",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Student not found", content = @Content) })
    public ResponseEntity<?> deleteStudent(@Parameter(description="Student id for adding. Cannot be null or empty.",
                                            required=true, schema=@Schema(name = "id", implementation = String.class))
                                            @PathVariable("id") Student student) {
        if(student == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer id = student.getId();
        universityService.removeStudentById(id);
        return ResponseEntity.ok("Student is removed!");
    }


    @PostMapping(value = "students")
    @Operation(summary = "Adding new student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content)})
    @JsonView({Views.FullSchedule.class})
    public ResponseEntity<Object> addNewStudent(@Parameter(description="Student for adding. Cannot be null or empty.",
                                                required=true, schema=@Schema(implementation = StudentDto.class))
                                                @RequestBody @Valid StudentDto studentDto,
                                                BindingResult bindingResult) throws PSQLException {
        if (studentDto.getStudent() == null || studentDto.getGroupId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Student student = studentDto.getStudent();
        Student storageStudent = universityService.findStudentByFullName(student.getFirstName(), student.getLastName());
        boolean isExists = storageStudent != null;
        if(isExists){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Identity constraint", "This student is already exists!");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        student.setGroup(universityService.getGroupById(studentDto.getGroupId()));
        Integer id = universityService.addStudent(student);
        student.setId(id);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }


    @PutMapping(value = "students/{id}")
    @Operation(summary = "Updating student by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student is updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid data",
                    content = @Content) })
    @JsonView({Views.IdName.class})
    public ResponseEntity<Object> updateStudent(@Parameter(description = "id of student to be updating",
                                                schema = @Schema(name = "id", implementation = String.class))
                                                @PathVariable(name = "id") Student studentFromDB,
                                                @Parameter(description="Student for adding. Cannot be null or empty.",
                                                required=true, schema=@Schema(implementation = StudentDto.class))
                                                @RequestBody @Valid StudentDto studentDto,
                                                BindingResult bindingResult) throws PSQLException {
        if (studentFromDB == null || studentDto.getStudent() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(studentDto.getStudent(), studentFromDB, "id", "faculty", "group");
        studentFromDB.setGroup(universityService.getGroupById(studentDto.getGroupId()));
        universityService.addStudent(studentFromDB);
        return ResponseEntity.ok(studentFromDB);
    }
}
