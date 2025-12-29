package com.academix.student.service;

import com.academix.student.dto.AttendanceDtos.*;
import com.academix.student.entity.Attendance;
import com.academix.student.entity.AttendanceStatus;
import com.academix.student.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public QRCodeResponse generateQRCode(Long courseId, String courseName) {
        String qrCode = "ATT-" + courseId + "-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String qrData = qrCode + "|" + courseId + "|" + LocalDate.now();

        return QRCodeResponse.builder()
                .qrCode(qrCode)
                .qrCodeData(Base64.getEncoder().encodeToString(qrData.getBytes()))
                .courseId(courseId)
                .courseName(courseName)
                .date(LocalDate.now())
                .expiresAt(LocalDateTime.now().plusHours(3).toString())
                .build();
    }

    @Transactional
    public AttendanceResponse markAttendanceByQR(MarkAttendanceByQRRequest request) {
        try {
            String decoded = new String(Base64.getDecoder().decode(request.getQrCode()));
            String[] parts = decoded.split("\\|");

            if (parts.length < 3) {
                throw new RuntimeException("QR Code invalide");
            }

            String qrCode = parts[0];
            Long courseId = Long.parseLong(parts[1]);
            LocalDate date = LocalDate.parse(parts[2]);

            Optional<Attendance> existing = attendanceRepository
                    .findByStudentIdAndCourseIdAndAttendanceDate(request.getStudentId(), courseId, date);

            if (existing.isPresent()) {
                throw new RuntimeException("Présence déjà enregistrée pour ce cours aujourd'hui");
            }

            Attendance attendance = Attendance.builder()
                    .studentId(request.getStudentId())
                    .courseId(courseId)
                    .attendanceDate(date)
                    .checkInTime(LocalTime.now())
                    .status(AttendanceStatus.PRESENT)
                    .qrCode(qrCode)
                    .isJustified(false)
                    .build();

            attendanceRepository.save(attendance);
            return mapToResponse(attendance);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du scan: " + e.getMessage());
        }
    }

    @Transactional
    public AttendanceResponse markAttendance(CreateAttendanceRequest request) {
        Optional<Attendance> existing = attendanceRepository
                .findByStudentIdAndCourseIdAndAttendanceDate(
                        request.getStudentId(),
                        request.getCourseId(),
                        request.getAttendanceDate()
                );

        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setStatus(request.getStatus());
            attendance.setMarkedBy(request.getMarkedBy());
        } else {
            attendance = Attendance.builder()
                    .studentId(request.getStudentId())
                    .courseId(request.getCourseId())
                    .scheduleId(request.getScheduleId())
                    .attendanceDate(request.getAttendanceDate())
                    .status(request.getStatus())
                    .markedBy(request.getMarkedBy())
                    .checkInTime(request.getStatus() == AttendanceStatus.PRESENT ? LocalTime.now() : null)
                    .isJustified(false)
                    .build();
        }

        attendanceRepository.save(attendance);
        return mapToResponse(attendance);
    }

    @Transactional
    public List<AttendanceResponse> markBulkAttendance(BulkAttendanceRequest request) {
        List<AttendanceResponse> responses = new ArrayList<>();

        for (StudentAttendance sa : request.getStudents()) {
            CreateAttendanceRequest car = CreateAttendanceRequest.builder()
                    .studentId(sa.getStudentId())
                    .courseId(request.getCourseId())
                    .attendanceDate(request.getAttendanceDate())
                    .status(sa.getStatus())
                    .markedBy(request.getMarkedBy())
                    .build();

            responses.add(markAttendance(car));
        }

        return responses;
    }

    @Transactional
    public AttendanceResponse justifyAbsence(JustifyAbsenceRequest request) {
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        attendance.setIsJustified(true);
        attendance.setJustification(request.getJustification());
        attendance.setJustificationFile(request.getJustificationFile());
        attendance.setStatus(AttendanceStatus.EXCUSED);

        attendanceRepository.save(attendance);
        return mapToResponse(attendance);
    }

    public List<AttendanceResponse> getStudentAttendance(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> getCourseAttendance(Long courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndAttendanceDate(courseId, date)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAllByOrderByAttendanceDateDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AttendanceStatsResponse getStudentStats(Long studentId) {
        int total = attendanceRepository.countTotalByStudent(studentId);
        int present = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        int absent = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        int late = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);
        int excused = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.EXCUSED);

        double rate = total > 0 ? ((double) (present + late) / total) * 100 : 0;

        return AttendanceStatsResponse.builder()
                .studentId(studentId)
                .totalClasses(total)
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .excusedCount(excused)
                .attendanceRate(Math.round(rate * 100.0) / 100.0)
                .build();
    }

    public AttendanceStatsResponse getStudentStatsByCourse(Long studentId, Long courseId) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);

        int total = attendances.size();
        int present = (int) attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        int absent = (int) attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        int late = (int) attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        int excused = (int) attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();

        double rate = total > 0 ? ((double) (present + late) / total) * 100 : 0;

        return AttendanceStatsResponse.builder()
                .studentId(studentId)
                .totalClasses(total)
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .excusedCount(excused)
                .attendanceRate(Math.round(rate * 100.0) / 100.0)
                .build();
    }

    private AttendanceResponse mapToResponse(Attendance a) {
        return AttendanceResponse.builder()
                .id(a.getId())
                .studentId(a.getStudentId())
                .courseId(a.getCourseId())
                .attendanceDate(a.getAttendanceDate())
                .checkInTime(a.getCheckInTime())
                .status(a.getStatus())
                .isJustified(a.getIsJustified())
                .justification(a.getJustification())
                .qrCode(a.getQrCode())
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .build();
    }
}