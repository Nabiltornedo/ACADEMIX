package com.academix.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    private Double score;
    
    @Column(name = "is_present")
    private boolean isPresent = true;
    
    @Enumerated(EnumType.STRING)
    private ResultStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "graded_by")
    private Long gradedBy;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ResultStatus.PENDING;
    }
}
