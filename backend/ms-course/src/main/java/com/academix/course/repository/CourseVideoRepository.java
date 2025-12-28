package com.academix.course.repository;

import com.academix.course.entity.CourseVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseVideoRepository extends JpaRepository<CourseVideo, Long> {

    List<CourseVideo> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    List<CourseVideo> findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(Long courseId);

    int countByCourseId(Long courseId);
}
