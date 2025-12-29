package com.academix.student.controller;

import com.academix.student.dto.GradeDtos;
import com.academix.student.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public ResponseEntity<List<GradeDtos.GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDtos.GradeResponse> getGradeById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDtos.GradeResponse>> getGradesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<GradeDtos.GradeResponse>> getGradesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<GradeDtos.GradeResponse>> getGradesByStudentAndSemester(
            @PathVariable Long studentId, @PathVariable Integer semester) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndSemester(studentId, semester));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<GradeDtos.GradeResponse>> getGradesByStudentAndCourse(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndCourse(studentId, courseId));
    }

    @PostMapping
    public ResponseEntity<GradeDtos.GradeResponse> createGrade(@RequestBody GradeDtos.CreateGradeRequest request) {
        return ResponseEntity.ok(gradeService.createGrade(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDtos.GradeResponse> updateGrade(@PathVariable Long id, @RequestBody GradeDtos.UpdateGradeRequest request) {
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
            @PathVariable Long studentId, @PathVariable Integer semester) {
        Double average = gradeService.getStudentSemesterAverage(studentId, semester);
        return ResponseEntity.ok(Map.of("average", average != null ? average : 0.0));
    }

    @GetMapping("/student/{studentId}/course/{courseId}/average")
    public ResponseEntity<Map<String, Object>> getCourseAverage(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        Map<String, Object> result = new HashMap<>();
        result.put("average", gradeService.getCourseAverage(studentId, courseId));
        result.put("classAverage", gradeService.getClassAverage(courseId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{studentId}/averages")
    public ResponseEntity<GradeDtos.StudentAveragesResponse> getStudentAverages(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getStudentAverages(studentId));
    }

    @GetMapping("/student/{studentId}/semester/{semester}/report")
    public ResponseEntity<GradeDtos.SemesterReport> getSemesterReport(
            @PathVariable Long studentId,
            @PathVariable Integer semester,
            @RequestParam(defaultValue = "2024-2025") String academicYear) {
        return ResponseEntity.ok(gradeService.getSemesterReport(studentId, semester, academicYear));
    }
}