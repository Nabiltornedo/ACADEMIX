package com.academix.student.repository;

import com.academix.student.entity.Grade;
import com.academix.student.entity.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByCourseId(Long courseId);
    List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Grade> findByStudentIdAndSemester(Long studentId, String semester);
    List<Grade> findByStudentIdAndGradeType(Long studentId, GradeType gradeType);
    
    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.studentId = :studentId")
    Double calculateAverageByStudentId(Long studentId);
    
    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.studentId = :studentId AND g.semester = :semester")
    Double calculateAverageByStudentIdAndSemester(Long studentId, String semester);
}
