package com.academix.student.service;

import com.academix.student.dto.StudentDtos.*;
import com.academix.student.entity.Grade;
import com.academix.student.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {
    
    private final GradeRepository gradeRepository;
    
    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return mapToResponse(grade);
    }
    
    public List<GradeResponse> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<GradeResponse> getGradesByCourse(Long courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<GradeResponse> getGradesByStudentAndSemester(Long studentId, String semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public GradeResponse createGrade(CreateGradeRequest request) {
        Grade grade = Grade.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .examId(request.getExamId())
                .score(request.getScore())
                .maxScore(request.getMaxScore() != null ? request.getMaxScore() : 20.0)
                .gradeType(request.getGradeType())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .comments(request.getComments())
                .gradedBy(request.getGradedBy())
                .gradedAt(LocalDateTime.now())
                .build();
        
        gradeRepository.save(grade);
        return mapToResponse(grade);
    }
    
    @Transactional
    public GradeResponse updateGrade(Long id, CreateGradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        if (request.getScore() != null) grade.setScore(request.getScore());
        if (request.getMaxScore() != null) grade.setMaxScore(request.getMaxScore());
        if (request.getGradeType() != null) grade.setGradeType(request.getGradeType());
        if (request.getComments() != null) grade.setComments(request.getComments());
        grade.setGradedAt(LocalDateTime.now());
        
        gradeRepository.save(grade);
        return mapToResponse(grade);
    }
    
    @Transactional
    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new RuntimeException("Grade not found");
        }
        gradeRepository.deleteById(id);
    }
    
    public Double getStudentAverage(Long studentId) {
        return gradeRepository.calculateAverageByStudentId(studentId);
    }
    
    public Double getStudentSemesterAverage(Long studentId, String semester) {
        return gradeRepository.calculateAverageByStudentIdAndSemester(studentId, semester);
    }
    
    private GradeResponse mapToResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .courseId(grade.getCourseId())
                .examId(grade.getExamId())
                .score(grade.getScore())
                .maxScore(grade.getMaxScore())
                .gradeType(grade.getGradeType())
                .semester(grade.getSemester())
                .academicYear(grade.getAcademicYear())
                .comments(grade.getComments())
                .gradedBy(grade.getGradedBy())
                .gradedAt(grade.getGradedAt())
                .build();
    }
}
