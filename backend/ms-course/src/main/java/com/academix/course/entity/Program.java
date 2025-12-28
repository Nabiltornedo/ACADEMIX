package com.academix.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "programs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Program {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "program_code", nullable = false, unique = true)
    private String programCode;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String department;
    
    @Column(name = "duration_years")
    private Integer durationYears;
    
    @Column(name = "total_credits")
    private Integer totalCredits;
    
    @Enumerated(EnumType.STRING)
    private ProgramLevel level;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
