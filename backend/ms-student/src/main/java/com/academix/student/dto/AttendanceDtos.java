package com.academix.student.dto;

import com.academix.student.entity.AttendanceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateAttendanceRequest {
        private Long studentId;
        private Long courseId;
        private Long scheduleId;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private Long markedBy;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MarkAttendanceByQRRequest {
        private String qrCode;
        private Long studentId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BulkAttendanceRequest {
        private Long courseId;
        private LocalDate attendanceDate;
        private Long markedBy;
        private List<StudentAttendance> students;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentAttendance {
        private Long studentId;
        private AttendanceStatus status;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JustifyAbsenceRequest {
        private Long attendanceId;
        private String justification;
        private String justificationFile;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private Long courseId;
        private String courseName;
        private LocalDate attendanceDate;
        private LocalTime checkInTime;
        private AttendanceStatus status;
        private Boolean isJustified;
        private String justification;
        private String qrCode;
        private String createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceStatsResponse {
        private Long studentId;
        private String studentName;
        private int totalClasses;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private int excusedCount;
        private double attendanceRate;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QRCodeResponse {
        private String qrCode;
        private String qrCodeData;
        private Long courseId;
        private String courseName;
        private LocalDate date;
        private String expiresAt;
    }
}