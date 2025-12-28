package com.academix.student.repository;

import com.academix.student.entity.Student;
import com.academix.student.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUserId(Long userId);
    List<Student> findByStatus(StudentStatus status);
    List<Student> findByProgramId(Long programId);
    List<Student> findByCurrentSemester(Integer semester);
    boolean existsByStudentCode(String studentCode);
    boolean existsByEmail(String email);
}
