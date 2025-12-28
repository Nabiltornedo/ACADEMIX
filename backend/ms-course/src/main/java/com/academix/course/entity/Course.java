package com.academix.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer credits;

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "program_id")
    private Long programId;

    private Integer semester;

    @Enumerated(EnumType.STRING)
    private CourseType type;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @Column(name = "max_students")
    private Integer maxStudents;

    // ========== NOUVEAUX CHAMPS POUR IA ==========
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "summary_generated_at")
    private LocalDateTime summaryGeneratedAt;
    // =============================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = CourseStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}