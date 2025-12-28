package com.academix.teacher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "teacher_code", nullable = false, unique = true)
    private String teacherCode;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    private String address;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    private String department;
    private String specialization;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Enumerated(EnumType.STRING)
    private TeacherStatus status;
    
    @Column(name = "office_location")
    private String officeLocation;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = TeacherStatus.ACTIVE;
    }
    
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
