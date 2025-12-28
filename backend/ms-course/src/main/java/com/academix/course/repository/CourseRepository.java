package com.academix.course.repository;

import com.academix.course.entity.Course;
import com.academix.course.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByProgramId(Long programId);
    List<Course> findBySemester(Integer semester);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByProgramIdAndSemester(Long programId, Integer semester);
    boolean existsByCourseCode(String courseCode);
}
