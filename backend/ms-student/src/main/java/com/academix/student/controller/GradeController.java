package com.academix.student.controller;

import com.academix.student.dto.StudentDtos.*;
import com.academix.student.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradeService gradeService;
    
    @GetMapping
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GradeResponse> getGradeById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeResponse>> getGradesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<GradeResponse>> getGradesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }
    
    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<GradeResponse>> getGradesByStudentAndSemester(
            @PathVariable Long studentId, @PathVariable String semester) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndSemester(studentId, semester));
    }
    
    @PostMapping
    public ResponseEntity<GradeResponse> createGrade(@RequestBody CreateGradeRequest request) {
        return ResponseEntity.ok(gradeService.createGrade(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GradeResponse> updateGrade(@PathVariable Long id, @RequestBody CreateGradeRequest request) {
        return ResponseEntity.ok(gradeService.updateGrade(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Map<String, Double>> getStudentAverage(@PathVariable Long studentId) {
        Double average = gradeService.getStudentAverage(studentId);
        return ResponseEntity.ok(Map.of("average", average != null ? average : 0.0));
    }
    
    @GetMapping("/student/{studentId}/semester/{semester}/average")
    public ResponseEntity<Map<String, Double>> getStudentSemesterAverage(
            @PathVariable Long studentId, @PathVariable String semester) {
        Double average = gradeService.getStudentSemesterAverage(studentId, semester);
        return ResponseEntity.ok(Map.of("average", average != null ? average : 0.0));
    }
}
