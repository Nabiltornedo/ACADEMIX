package com.academix.course.dto;

import lombok.*;

public class CourseVideoDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateVideoRequest {
        private Long courseId;
        private String title;
        private String description;
        private String videoUrl;
        private String thumbnailUrl;
        private Integer durationMinutes;
        private Integer orderIndex;
        private Long uploadedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateVideoRequest {
        private String title;
        private String description;
        private String videoUrl;
        private String thumbnailUrl;
        private Integer durationMinutes;
        private Integer orderIndex;
        private Boolean isPublished;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoResponse {
        private Long id;
        private Long courseId;
        private String title;
        private String description;
        private String videoUrl;
        private String thumbnailUrl;
        private Integer durationMinutes;
        private Integer orderIndex;
        private Boolean isPublished;
        private Long uploadedBy;
        private String createdAt;
        private String updatedAt;
    }
}
