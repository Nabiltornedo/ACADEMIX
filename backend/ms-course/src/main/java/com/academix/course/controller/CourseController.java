package com.academix.course.controller;

import com.academix.course.dto.CourseDtos.*;
import com.academix.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    
    // Course endpoints
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() { return ResponseEntity.ok(courseService.getAllCourses()); }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) { return ResponseEntity.ok(courseService.getCourseById(id)); }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String code) { return ResponseEntity.ok(courseService.getCourseByCode(code)); }
    
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) { return ResponseEntity.ok(courseService.createCourse(request)); }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseRequest request) { return ResponseEntity.ok(courseService.updateCourse(id, request)); }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) { courseService.deleteCourse(id); return ResponseEntity.noContent().build(); }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByTeacher(@PathVariable Long teacherId) { return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId)); }
    
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByProgram(@PathVariable Long programId) { return ResponseEntity.ok(courseService.getCoursesByProgram(programId)); }
    
    // Program endpoints
    @GetMapping("/programs")
    public ResponseEntity<List<ProgramResponse>> getAllPrograms() { return ResponseEntity.ok(courseService.getAllPrograms()); }
    
    @GetMapping("/programs/{id}")
    public ResponseEntity<ProgramResponse> getProgramById(@PathVariable Long id) { return ResponseEntity.ok(courseService.getProgramById(id)); }
    
    @PostMapping("/programs")
    public ResponseEntity<ProgramResponse> createProgram(@Valid @RequestBody CreateProgramRequest request) { return ResponseEntity.ok(courseService.createProgram(request)); }
    
    @DeleteMapping("/programs/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id) { courseService.deleteProgram(id); return ResponseEntity.noContent().build(); }
    
    // Enrollment endpoints
    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByStudent(@PathVariable Long studentId) { return ResponseEntity.ok(courseService.getEnrollmentsByStudent(studentId)); }
    
    @GetMapping("/enrollments/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(@PathVariable Long courseId) { return ResponseEntity.ok(courseService.getEnrollmentsByCourse(courseId)); }
    
    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@RequestBody CreateEnrollmentRequest request) { return ResponseEntity.ok(courseService.enrollStudent(request)); }
    
    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> dropEnrollment(@PathVariable Long id) { courseService.dropEnrollment(id); return ResponseEntity.noContent().build(); }
}
