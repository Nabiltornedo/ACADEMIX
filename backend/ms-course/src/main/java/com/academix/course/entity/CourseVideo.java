package com.academix.course.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String videoUrl;

    private String thumbnailUrl;

    private Integer durationMinutes;

    private Integer orderIndex;

    @Column(nullable = false)
    private Boolean isPublished = true;

    private Long uploadedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
