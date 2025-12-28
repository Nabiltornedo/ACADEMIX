package com.academix.course.dto;

import com.academix.course.entity.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class CourseDtos {
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateCourseRequest {
        @NotBlank(message = "Course code is required")
        private String courseCode;
        @NotBlank(message = "Course name is required")
        private String name;
        private String description;
        private Integer credits;
        private Integer hoursPerWeek;
        private Long teacherId;
        private Long programId;
        private Integer semester;
        private CourseType type;
        private Integer maxStudents;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateCourseRequest {
        private String name;
        private String description;
        private Integer credits;
        private Integer hoursPerWeek;
        private Long teacherId;
        private Long programId;
        private Integer semester;
        private CourseType type;
        private CourseStatus status;
        private Integer maxStudents;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CourseResponse {
        private Long id;
        private String courseCode;
        private String name;
        private String description;
        private Integer credits;
        private Integer hoursPerWeek;
        private Long teacherId;
        private Long programId;
        private Integer semester;
        private CourseType type;
        private CourseStatus status;
        private Integer maxStudents;
        private Integer enrolledCount;
        private LocalDateTime createdAt;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateProgramRequest {
        @NotBlank(message = "Program code is required")
        private String programCode;
        @NotBlank(message = "Program name is required")
        private String name;
        private String description;
        private String department;
        private Integer durationYears;
        private Integer totalCredits;
        private ProgramLevel level;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProgramResponse {
        private Long id;
        private String programCode;
        private String name;
        private String description;
        private String department;
        private Integer durationYears;
        private Integer totalCredits;
        private ProgramLevel level;
        private boolean isActive;
        private LocalDateTime createdAt;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateEnrollmentRequest {
        private Long studentId;
        private Long courseId;
        private String academicYear;
        private String semester;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EnrollmentResponse {
        private Long id;
        private Long studentId;
        private Long courseId;
        private String academicYear;
        private String semester;
        private EnrollmentStatus status;
        private LocalDateTime enrollmentDate;
    }
}
