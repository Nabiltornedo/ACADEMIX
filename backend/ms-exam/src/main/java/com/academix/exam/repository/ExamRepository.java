package com.academix.exam.repository;

import com.academix.exam.entity.Exam;
import com.academix.exam.entity.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByExamCode(String examCode);
    List<Exam> findByCourseId(Long courseId);
    List<Exam> findByExamDate(LocalDate examDate);
    List<Exam> findByStatus(ExamStatus status);
    List<Exam> findBySemester(String semester);
    List<Exam> findByExamDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByExamCode(String examCode);
}
