package com.academix.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    private String semester;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
    
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
    
    @PrePersist
    protected void onCreate() {
        enrollmentDate = LocalDateTime.now();
        if (status == null) status = EnrollmentStatus.ENROLLED;
    }
}
