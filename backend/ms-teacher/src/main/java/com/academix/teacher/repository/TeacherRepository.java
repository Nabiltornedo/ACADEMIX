package com.academix.teacher.repository;

import com.academix.teacher.entity.Teacher;
import com.academix.teacher.entity.TeacherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherCode(String teacherCode);
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findByStatus(TeacherStatus status);
    List<Teacher> findByDepartment(String department);
    boolean existsByTeacherCode(String teacherCode);
    boolean existsByEmail(String email);
}
