package com.academix.student.dto;

import com.academix.student.entity.GradeType;
import lombok.*;
import java.util.List;

public class GradeDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateGradeRequest {
        private Long studentId;
        private Long courseId;
        private Long examId;
        private GradeType gradeType;
        private Double score;
        private Double maxScore;
        private Double coefficient;
        private Integer semester;
        private String academicYear;
        private String comments;
        private Long gradedBy;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateGradeRequest {
        private Double score;
        private Double maxScore;
        private String comments;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GradeResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private Long courseId;
        private String courseName;
        private Long examId;
        private GradeType gradeType;
        private Double score;
        private Double maxScore;
        private Double coefficient;
        private Double normalizedScore;
        private Integer semester;
        private String academicYear;
        private String comments;
        private Long gradedBy;      // <-- CE CHAMP ÉTAIT MANQUANT
        private String gradedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CourseGradesSummary {
        private Long courseId;
        private String courseName;
        private String courseCode;
        private Integer credits;
        private List<GradeResponse> grades;
        private Double average;
        private Double classAverage;
        private String status;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SemesterReport {
        private Long studentId;
        private String studentName;
        private String studentCode;
        private Integer semester;
        private String academicYear;
        private List<CourseGradesSummary> courses;
        private Double semesterAverage;
        private Integer totalCredits;
        private Integer earnedCredits;
        private String mention;
        private Integer rank;
        private Integer totalStudents;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentAveragesResponse {
        private Long studentId;
        private Double overallAverage;
        private String mention;
        private List<CourseAverage> courseAverages;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CourseAverage {
        private Long courseId;
        private String courseName;
        private Double average;
        private String status;
    }
}