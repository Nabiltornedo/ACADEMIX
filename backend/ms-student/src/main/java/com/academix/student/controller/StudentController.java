package com.academix.student.controller;

import com.academix.student.dto.StudentDtos.*;
import com.academix.student.entity.StudentStatus;
import com.academix.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<StudentResponse> getStudentByCode(@PathVariable String code) {
        return ResponseEntity.ok(studentService.getStudentByCode(code));
    }
    
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentResponse>> getStudentsByStatus(@PathVariable StudentStatus status) {
        return ResponseEntity.ok(studentService.getStudentsByStatus(status));
    }
    
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(studentService.getStudentsByProgram(programId));
    }
}
