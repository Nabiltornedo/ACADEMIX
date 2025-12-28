package com.academix.schedule.service;

import com.academix.schedule.dto.ScheduleDtos.*;
import com.academix.schedule.entity.*;
import com.academix.schedule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;

    // Schedule methods
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(this::mapScheduleToResponse).collect(Collectors.toList());
    }

    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Schedule not found"));
        return mapScheduleToResponse(schedule);
    }

    public List<ScheduleResponse> getSchedulesByCourse(Long courseId) {
        return scheduleRepository.findByCourseId(courseId).stream().map(this::mapScheduleToResponse).collect(Collectors.toList());
    }

    public List<ScheduleResponse> getSchedulesByTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId).stream().map(this::mapScheduleToResponse).collect(Collectors.toList());
    }

    public List<ScheduleResponse> getSchedulesByRoom(Long roomId) {
        return scheduleRepository.findByRoomId(roomId).stream().map(this::mapScheduleToResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (request.getName() != null) room.setName(request.getName());
        if (request.getBuilding() != null) room.setBuilding(request.getBuilding());
        if (request.getFloor() != null) room.setFloor(request.getFloor());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getType() != null) room.setType(request.getType());
        if (request.getHasProjector() != null) room.setHasProjector(request.getHasProjector());
        if (request.getHasComputer() != null) room.setHasComputer(request.getHasComputer());
        if (request.getIsAvailable() != null) room.setIsAvailable(request.getIsAvailable());

        roomRepository.save(room);
        return mapRoomToResponse(room);
    }

    @Transactional
    public ScheduleResponse createSchedule(CreateScheduleRequest request) {
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                request.getRoomId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
        if (!conflicts.isEmpty()) throw new RuntimeException("Room is not available at this time");

        Schedule schedule = Schedule.builder()
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .roomId(request.getRoomId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .type(request.getType())
                .isRecurring(request.getIsRecurring())
                .academicYear(request.getAcademicYear())
                .semester(request.getSemester())
                .build();

        scheduleRepository.save(schedule);
        return mapScheduleToResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) throw new RuntimeException("Schedule not found");
        scheduleRepository.deleteById(id);
    }

    // Room methods
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(this::mapRoomToResponse).collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        return mapRoomToResponse(room);
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findByIsAvailableTrue().stream().map(this::mapRoomToResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        if (roomRepository.existsByRoomCode(request.getRoomCode())) throw new RuntimeException("Room code already exists");

        Room room = Room.builder()
                .roomCode(request.getRoomCode())
                .name(request.getName())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .capacity(request.getCapacity())
                .type(request.getType())
                .hasProjector(request.getHasProjector())
                .hasComputer(request.getHasComputer())
                .isAvailable(true)
                .build();

        roomRepository.save(room);
        return mapRoomToResponse(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) throw new RuntimeException("Room not found");
        roomRepository.deleteById(id);
    }

    private ScheduleResponse mapScheduleToResponse(Schedule s) {
        return ScheduleResponse.builder()
                .id(s.getId()).courseId(s.getCourseId()).teacherId(s.getTeacherId()).roomId(s.getRoomId())
                .dayOfWeek(s.getDayOfWeek()).startTime(s.getStartTime()).endTime(s.getEndTime())
                .startDate(s.getStartDate()).endDate(s.getEndDate()).type(s.getType())
                .isRecurring(s.getIsRecurring()).academicYear(s.getAcademicYear()).semester(s.getSemester())
                .createdAt(s.getCreatedAt()).build();
    }

    private RoomResponse mapRoomToResponse(Room r) {
        return RoomResponse.builder()
                .id(r.getId()).roomCode(r.getRoomCode()).name(r.getName()).building(r.getBuilding())
                .floor(r.getFloor()).capacity(r.getCapacity()).type(r.getType())
                .hasProjector(r.getHasProjector()).hasComputer(r.getHasComputer()).isAvailable(r.getIsAvailable()).build();
    }
}