package com.academix.student.controller;

import com.academix.student.dto.AttendanceDtos.*;
import com.academix.student.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/students/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PostMapping("/qr-code/generate")
    public ResponseEntity<QRCodeResponse> generateQRCode(
            @RequestParam Long courseId,
            @RequestParam(required = false) String courseName) {
        return ResponseEntity.ok(attendanceService.generateQRCode(courseId, courseName));
    }

    @PostMapping("/qr-code/scan")
    public ResponseEntity<AttendanceResponse> markByQRCode(@RequestBody MarkAttendanceByQRRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendanceByQR(request));
    }

    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponse> markAttendance(@RequestBody CreateAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(request));
    }

    @PostMapping("/mark-bulk")
    public ResponseEntity<List<AttendanceResponse>> markBulkAttendance(@RequestBody BulkAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.markBulkAttendance(request));
    }

    @PostMapping("/justify")
    public ResponseEntity<AttendanceResponse> justifyAbsence(@RequestBody JustifyAbsenceRequest request) {
        return ResponseEntity.ok(attendanceService.justifyAbsence(request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AttendanceResponse>> getCourseAttendance(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getCourseAttendance(courseId, date));
    }

    @GetMapping("/stats/student/{studentId}")
    public ResponseEntity<AttendanceStatsResponse> getStudentStats(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getStudentStats(studentId));
    }

    @GetMapping("/stats/student/{studentId}/course/{courseId}")
    public ResponseEntity<AttendanceStatsResponse> getStudentStatsByCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getStudentStatsByCourse(studentId, courseId));
    }
}