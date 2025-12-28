package com.academix.teacher.dto;

import com.academix.teacher.entity.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TeacherDtos {
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateTeacherRequest {
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
        private String department;
        private String specialization;
        private String officeLocation;
        private Long userId;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateTeacherRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String department;
        private String specialization;
        private String officeLocation;
        private TeacherStatus status;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TeacherResponse {
        private Long id;
        private String teacherCode;
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private String department;
        private String specialization;
        private LocalDate hireDate;
        private TeacherStatus status;
        private String officeLocation;
        private LocalDateTime createdAt;
        private List<AvailabilityResponse> availabilities;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateAvailabilityRequest {
        private Long teacherId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean isAvailable;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AvailabilityResponse {
        private Long id;
        private Long teacherId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean isAvailable;
    }
}
