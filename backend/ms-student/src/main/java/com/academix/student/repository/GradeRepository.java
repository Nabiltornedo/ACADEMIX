package com.academix.student.repository;

import com.academix.student.entity.Grade;
import com.academix.student.entity.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByCourseId(Long courseId);

    List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);

    // MODIFIÉ: Integer au lieu de String
    List<Grade> findByStudentIdAndSemester(Long studentId, Integer semester);

    List<Grade> findByStudentIdAndGradeType(Long studentId, GradeType gradeType);

    List<Grade> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    List<Grade> findByExamId(Long examId);

    List<Grade> findByCourseIdAndGradeType(Long courseId, GradeType gradeType);

    // MODIFIÉ: Calcul normalisé sur 20
    @Query("SELECT AVG(g.score / g.maxScore * 20) FROM Grade g WHERE g.studentId = :studentId")
    Double calculateAverageByStudentId(@Param("studentId") Long studentId);

    // MODIFIÉ: Integer semester + calcul normalisé
    @Query("SELECT AVG(g.score / g.maxScore * 20) FROM Grade g WHERE g.studentId = :studentId AND g.semester = :semester")
    Double calculateAverageByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") Integer semester);

    // NOUVEAU: Moyenne d'un étudiant pour un cours
    @Query("SELECT AVG(g.score / g.maxScore * 20) FROM Grade g WHERE g.studentId = :studentId AND g.courseId = :courseId")
    Double calculateAverageByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    // NOUVEAU: Moyenne de la classe pour un cours
    @Query("SELECT AVG(g.score / g.maxScore * 20) FROM Grade g WHERE g.courseId = :courseId")
    Double calculateClassAverageByCourseId(@Param("courseId") Long courseId);
}