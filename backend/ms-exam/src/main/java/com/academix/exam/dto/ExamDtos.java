package com.academix.exam.dto;

import com.academix.exam.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class ExamDtos {
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateExamRequest {
        private String title;
        private Long courseId;
        private Long roomId;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer durationMinutes;
        private ExamType type;
        private Double maxScore;
        private Double passingScore;
        private String instructions;
        private String academicYear;
        private String semester;
        private Long createdBy;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateExamRequest {
        private String title;
        private Long roomId;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private ExamStatus status;
        private Double maxScore;
        private Double passingScore;
        private String instructions;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ExamResponse {
        private Long id;
        private String examCode;
        private String title;
        private Long courseId;
        private Long roomId;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer durationMinutes;
        private ExamType type;
        private ExamStatus status;
        private Double maxScore;
        private Double passingScore;
        private String instructions;
        private String academicYear;
        private String semester;
        private Long createdBy;
        private LocalDateTime createdAt;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateResultRequest {
        private Long examId;
        private Long studentId;
        private Double score;
        private boolean isPresent;
        private String comments;
        private Long gradedBy;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ExamResultResponse {
        private Long id;
        private Long examId;
        private Long studentId;
        private Double score;
        private boolean isPresent;
        private ResultStatus status;
        private String comments;
        private Long gradedBy;
        private LocalDateTime gradedAt;
    }
}
