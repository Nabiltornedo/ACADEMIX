package com.academix.exam.repository;

import com.academix.exam.entity.ExamResult;
import com.academix.exam.entity.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByStudentId(Long studentId);
    Optional<ExamResult> findByExamIdAndStudentId(Long examId, Long studentId);
    List<ExamResult> findByExamIdAndStatus(Long examId, ResultStatus status);
    
    @Query("SELECT AVG(r.score) FROM ExamResult r WHERE r.examId = :examId AND r.isPresent = true")
    Double calculateAverageByExamId(Long examId);
    
    @Query("SELECT COUNT(r) FROM ExamResult r WHERE r.examId = :examId AND r.status = :status")
    long countByExamIdAndStatus(Long examId, ResultStatus status);
}
