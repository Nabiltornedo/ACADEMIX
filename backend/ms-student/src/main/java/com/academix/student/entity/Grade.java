package com.academix.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "exam_id")
    private Long examId;

    @Column(nullable = false)
    private Double score;

    @Column(name = "max_score")
    private Double maxScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type")
    private GradeType gradeType;

    // MODIFIÉ: Integer au lieu de String pour les calculs
    @Column(name = "semester")
    private Integer semester;

    @Column(name = "academic_year")
    private String academicYear;

    // NOUVEAU: Coefficient pour pondérer les notes
    @Column(name = "coefficient")
    private Double coefficient;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "graded_by")
    private Long gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        gradedAt = LocalDateTime.now();
        if (maxScore == null) maxScore = 20.0;
        if (coefficient == null) coefficient = 1.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}