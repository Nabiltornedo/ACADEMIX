package com.academix.student.service;

import com.academix.student.dto.GradeDtos;
import com.academix.student.entity.Grade;
import com.academix.student.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    public List<GradeDtos.GradeResponse> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public GradeDtos.GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return mapToResponse(grade);
    }

    public List<GradeDtos.GradeResponse> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GradeDtos.GradeResponse> getGradesByCourse(Long courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GradeDtos.GradeResponse> getGradesByStudentAndCourse(Long studentId, Long courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GradeDtos.GradeResponse> getGradesByStudentAndSemester(Long studentId, Integer semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDtos.GradeResponse createGrade(GradeDtos.CreateGradeRequest request) {
        Grade grade = Grade.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .examId(request.getExamId())
                .score(request.getScore())
                .maxScore(request.getMaxScore() != null ? request.getMaxScore() : 20.0)
                .coefficient(request.getCoefficient() != null ? request.getCoefficient() : 1.0)
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
    public GradeDtos.GradeResponse updateGrade(Long id, GradeDtos.UpdateGradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));

        if (request.getScore() != null) grade.setScore(request.getScore());
        if (request.getMaxScore() != null) grade.setMaxScore(request.getMaxScore());
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

    // ==================== MÉTHODES POUR LES MOYENNES ====================

    public Double getStudentAverage(Long studentId) {
        Double avg = gradeRepository.calculateAverageByStudentId(studentId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : null;
    }

    public Double getStudentSemesterAverage(Long studentId, Integer semester) {
        Double avg = gradeRepository.calculateAverageByStudentIdAndSemester(studentId, semester);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : null;
    }

    public Double getCourseAverage(Long studentId, Long courseId) {
        Double avg = gradeRepository.calculateAverageByStudentIdAndCourseId(studentId, courseId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : null;
    }

    public Double getClassAverage(Long courseId) {
        Double avg = gradeRepository.calculateClassAverageByCourseId(courseId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : null;
    }

    // ==================== MÉTHODES POUR LE BULLETIN ====================

    public GradeDtos.StudentAveragesResponse getStudentAverages(Long studentId) {
        Double overallAvg = getStudentAverage(studentId);
        String mention = getMention(overallAvg);

        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        Map<Long, List<Grade>> gradesByCourse = grades.stream()
                .collect(Collectors.groupingBy(Grade::getCourseId));

        List<GradeDtos.CourseAverage> courseAverages = new ArrayList<>();
        for (Map.Entry<Long, List<Grade>> entry : gradesByCourse.entrySet()) {
            Double avg = getCourseAverage(studentId, entry.getKey());
            courseAverages.add(GradeDtos.CourseAverage.builder()
                    .courseId(entry.getKey())
                    .average(avg)
                    .status(avg != null && avg >= 10 ? "PASSED" : "FAILED")
                    .build());
        }

        return GradeDtos.StudentAveragesResponse.builder()
                .studentId(studentId)
                .overallAverage(overallAvg)
                .mention(mention)
                .courseAverages(courseAverages)
                .build();
    }

    public GradeDtos.SemesterReport getSemesterReport(Long studentId, Integer semester, String academicYear) {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(studentId, semester);

        Map<Long, List<Grade>> gradesByCourse = grades.stream()
                .collect(Collectors.groupingBy(Grade::getCourseId));

        List<GradeDtos.CourseGradesSummary> courseSummaries = new ArrayList<>();
        int totalCredits = 0;
        int earnedCredits = 0;

        for (Map.Entry<Long, List<Grade>> entry : gradesByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<Grade> courseGrades = entry.getValue();

            Double avg = getCourseAverage(studentId, courseId);
            Double classAvg = getClassAverage(courseId);
            String status = avg != null && avg >= 10 ? "PASSED" : "FAILED";
            int credits = 3;

            totalCredits += credits;
            if (avg != null && avg >= 10) earnedCredits += credits;

            GradeDtos.CourseGradesSummary summary = GradeDtos.CourseGradesSummary.builder()
                    .courseId(courseId)
                    .grades(courseGrades.stream().map(this::mapToResponse).collect(Collectors.toList()))
                    .average(avg)
                    .classAverage(classAvg)
                    .status(status)
                    .credits(credits)
                    .build();

            courseSummaries.add(summary);
        }

        Double semesterAvg = getStudentSemesterAverage(studentId, semester);
        String mention = getMention(semesterAvg);

        return GradeDtos.SemesterReport.builder()
                .studentId(studentId)
                .semester(semester)
                .academicYear(academicYear)
                .courses(courseSummaries)
                .semesterAverage(semesterAvg)
                .totalCredits(totalCredits)
                .earnedCredits(earnedCredits)
                .mention(mention)
                .build();
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private String getMention(Double average) {
        if (average == null) return "N/A";
        if (average >= 16) return "Très Bien";
        if (average >= 14) return "Bien";
        if (average >= 12) return "Assez Bien";
        if (average >= 10) return "Passable";
        return "Insuffisant";
    }

    private GradeDtos.GradeResponse mapToResponse(Grade grade) {
        Double normalizedScore = grade.getMaxScore() != null && grade.getMaxScore() > 0
                ? (grade.getScore() / grade.getMaxScore()) * 20
                : grade.getScore();
        normalizedScore = Math.round(normalizedScore * 100.0) / 100.0;

        return GradeDtos.GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .courseId(grade.getCourseId())
                .examId(grade.getExamId())
                .score(grade.getScore())
                .maxScore(grade.getMaxScore())
                .coefficient(grade.getCoefficient())
                .normalizedScore(normalizedScore)
                .gradeType(grade.getGradeType())
                .semester(grade.getSemester())
                .academicYear(grade.getAcademicYear())
                .comments(grade.getComments())
                .gradedBy(grade.getGradedBy())
                .gradedAt(grade.getGradedAt() != null ? grade.getGradedAt().toString() : null)
                .build();
    }
}