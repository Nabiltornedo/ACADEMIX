package com.academix.student.repository;

import com.academix.student.entity.Attendance;
import com.academix.student.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId);

    List<Attendance> findByCourseIdOrderByAttendanceDateDesc(Long courseId);

    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Attendance> findByCourseIdAndAttendanceDate(Long courseId, LocalDate date);

    Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDate(Long studentId, Long courseId, LocalDate date);

    Optional<Attendance> findByQrCode(String qrCode);

    int countByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, AttendanceStatus status);

    int countByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = 'PRESENT'")
    int countPresentByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId")
    int countTotalByStudent(@Param("studentId") Long studentId);

    List<Attendance> findAllByOrderByAttendanceDateDesc();
}