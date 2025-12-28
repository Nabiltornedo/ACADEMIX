package com.academix.teacher.repository;

import com.academix.teacher.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByTeacherId(Long teacherId);
    List<Availability> findByTeacherIdAndDayOfWeek(Long teacherId, DayOfWeek dayOfWeek);
    List<Availability> findByTeacherIdAndIsAvailable(Long teacherId, boolean isAvailable);
    void deleteByTeacherId(Long teacherId);
}
