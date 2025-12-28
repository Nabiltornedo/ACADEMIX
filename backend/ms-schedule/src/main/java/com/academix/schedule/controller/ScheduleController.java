package com.academix.schedule.controller;

import com.academix.schedule.dto.ScheduleDtos.*;
import com.academix.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() { return ResponseEntity.ok(scheduleService.getAllSchedules()); }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable Long id) { return ResponseEntity.ok(scheduleService.getScheduleById(id)); }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByCourse(@PathVariable Long courseId) { return ResponseEntity.ok(scheduleService.getSchedulesByCourse(courseId)); }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByTeacher(@PathVariable Long teacherId) { return ResponseEntity.ok(scheduleService.getSchedulesByTeacher(teacherId)); }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRoom(@PathVariable Long roomId) { return ResponseEntity.ok(scheduleService.getSchedulesByRoom(roomId)); }

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody CreateScheduleRequest request) { return ResponseEntity.ok(scheduleService.createSchedule(request)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) { scheduleService.deleteSchedule(id); return ResponseEntity.noContent().build(); }

    // Room endpoints
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() { return ResponseEntity.ok(scheduleService.getAllRooms()); }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) { return ResponseEntity.ok(scheduleService.getRoomById(id)); }

    @GetMapping("/rooms/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() { return ResponseEntity.ok(scheduleService.getAvailableRooms()); }

    @PostMapping("/rooms")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) { return ResponseEntity.ok(scheduleService.createRoom(request)); }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(scheduleService.updateRoom(id, request));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) { scheduleService.deleteRoom(id); return ResponseEntity.noContent().build(); }
}