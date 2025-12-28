package com.academix.exam.controller;

import com.academix.exam.dto.ExamDtos.*;
import com.academix.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    
    @GetMapping
    public ResponseEntity<List<ExamResponse>> getAllExams() { return ResponseEntity.ok(examService.getAllExams()); }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) { return ResponseEntity.ok(examService.getExamById(id)); }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ExamResponse>> getExamsByCourse(@PathVariable Long courseId) { return ResponseEntity.ok(examService.getExamsByCourse(courseId)); }
    
    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody CreateExamRequest request) { return ResponseEntity.ok(examService.createExam(request)); }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id, @RequestBody UpdateExamRequest request) { return ResponseEntity.ok(examService.updateExam(id, request)); }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) { examService.deleteExam(id); return ResponseEntity.noContent().build(); }
    
    // Result endpoints
    @GetMapping("/{examId}/results")
    public ResponseEntity<List<ExamResultResponse>> getResultsByExam(@PathVariable Long examId) { return ResponseEntity.ok(examService.getResultsByExam(examId)); }
    
    @GetMapping("/results/student/{studentId}")
    public ResponseEntity<List<ExamResultResponse>> getResultsByStudent(@PathVariable Long studentId) { return ResponseEntity.ok(examService.getResultsByStudent(studentId)); }
    
    @PostMapping("/results")
    public ResponseEntity<ExamResultResponse> submitResult(@RequestBody CreateResultRequest request) { return ResponseEntity.ok(examService.submitResult(request)); }
}
