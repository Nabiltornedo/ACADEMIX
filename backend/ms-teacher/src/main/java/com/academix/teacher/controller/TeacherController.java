package com.academix.teacher.controller;

import com.academix.teacher.dto.TeacherDtos.*;
import com.academix.teacher.entity.TeacherStatus;
import com.academix.teacher.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    
    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<TeacherResponse> getTeacherByCode(@PathVariable String code) {
        return ResponseEntity.ok(teacherService.getTeacherByCode(code));
    }
    
    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.createTeacher(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id, @RequestBody UpdateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<TeacherResponse>> getTeachersByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(teacherService.getTeachersByDepartment(department));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TeacherResponse>> getTeachersByStatus(@PathVariable TeacherStatus status) {
        return ResponseEntity.ok(teacherService.getTeachersByStatus(status));
    }
    
    // Availability endpoints
    @GetMapping("/{teacherId}/availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getTeacherAvailabilities(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getTeacherAvailabilities(teacherId));
    }
    
    @PostMapping("/availabilities")
    public ResponseEntity<AvailabilityResponse> addAvailability(@RequestBody CreateAvailabilityRequest request) {
        return ResponseEntity.ok(teacherService.addAvailability(request));
    }
    
    @DeleteMapping("/availabilities/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        teacherService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
