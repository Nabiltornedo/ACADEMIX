package com.academix.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class AdminDtos {
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateSettingRequest {
        private String settingKey;
        private String settingValue;
        private String description;
        private String settingType;
        private boolean isPublic;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateSettingRequest {
        private String settingValue;
        private String description;
        private boolean isPublic;
        private Long updatedBy;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SettingResponse {
        private Long id;
        private String settingKey;
        private String settingValue;
        private String description;
        private String settingType;
        private boolean isPublic;
        private Long updatedBy;
        private LocalDateTime updatedAt;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuditLogResponse {
        private Long id;
        private Long userId;
        private String username;
        private String action;
        private String entityType;
        private Long entityId;
        private String details;
        private String ipAddress;
        private LocalDateTime createdAt;
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DashboardStats {
        private long totalStudents;
        private long totalTeachers;
        private long totalCourses;
        private long activeExams;
        private long recentLogins;
    }
}
