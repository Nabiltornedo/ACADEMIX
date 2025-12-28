package com.academix.schedule.repository;

import com.academix.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCourseId(Long courseId);
    List<Schedule> findByTeacherId(Long teacherId);
    List<Schedule> findByRoomId(Long roomId);
    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<Schedule> findBySemester(String semester);
    
    @Query("SELECT s FROM Schedule s WHERE s.roomId = :roomId AND s.dayOfWeek = :dayOfWeek AND " +
           "((s.startTime <= :startTime AND s.endTime > :startTime) OR " +
           "(s.startTime < :endTime AND s.endTime >= :endTime) OR " +
           "(s.startTime >= :startTime AND s.endTime <= :endTime))")
    List<Schedule> findConflictingSchedules(Long roomId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
