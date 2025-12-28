package com.academix.student.dto;

import com.academix.student.entity.Gender;
import com.academix.student.entity.GradeType;
import com.academix.student.entity.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentDtos {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateStudentRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
        
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private Gender gender;
        private Long programId;
        private Long userId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStudentRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private Gender gender;
        private Long programId;
        private StudentStatus status;
        private Integer currentSemester;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentResponse {
        private Long id;
        private String studentCode;
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private Gender gender;
        private Long programId;
        private LocalDate enrollmentDate;
        private StudentStatus status;
        private Integer currentSemester;
        private Double gpa;
        private LocalDateTime createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateGradeRequest {
        private Long studentId;
        private Long courseId;
        private Long examId;
        private Double score;
        private Double maxScore;
        private GradeType gradeType;
        private String semester;
        private String academicYear;
        private String comments;
        private Long gradedBy;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeResponse {
        private Long id;
        private Long studentId;
        private Long courseId;
        private Long examId;
        private Double score;
        private Double maxScore;
        private GradeType gradeType;
        private String semester;
        private String academicYear;
        private String comments;
        private Long gradedBy;
        private LocalDateTime gradedAt;
    }
}
