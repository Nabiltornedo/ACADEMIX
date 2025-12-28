package com.academix.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Exam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_code", nullable = false, unique = true)
    private String examCode;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "room_id")
    private Long roomId;
    
    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Enumerated(EnumType.STRING)
    private ExamType type;
    
    @Enumerated(EnumType.STRING)
    private ExamStatus status;
    
    @Column(name = "max_score")
    private Double maxScore;
    
    @Column(name = "passing_score")
    private Double passingScore;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    private String semester;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ExamStatus.SCHEDULED;
    }
}
