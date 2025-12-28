package com.academix.exam.service;

import com.academix.exam.dto.ExamDtos.*;
import com.academix.exam.entity.*;
import com.academix.exam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamResultRepository resultRepository;

    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream().map(this::mapExamToResponse).collect(Collectors.toList());
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new RuntimeException("Exam not found"));
        return mapExamToResponse(exam);
    }

    public List<ExamResponse> getExamsByCourse(Long courseId) {
        return examRepository.findByCourseId(courseId).stream().map(this::mapExamToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ExamResponse createExam(CreateExamRequest request) {
        String examCode = "EXM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Exam exam = Exam.builder()
                .examCode(examCode)
                .title(request.getTitle())
                .courseId(request.getCourseId())
                .roomId(request.getRoomId())
                .examDate(request.getExamDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes(request.getDurationMinutes())
                .type(request.getType())
                .status(ExamStatus.SCHEDULED)
                .maxScore(request.getMaxScore() != null ? request.getMaxScore() : 20.0)
                .passingScore(request.getPassingScore() != null ? request.getPassingScore() : 10.0)
                .instructions(request.getInstructions())
                .academicYear(request.getAcademicYear())
                .semester(request.getSemester())
                .createdBy(request.getCreatedBy())
                .build();

        examRepository.save(exam);

        // ========== CRÉER LES NOTIFICATIONS ==========
        createExamNotifications(exam);
        // =============================================

        return mapExamToResponse(exam);
    }

    // Méthode pour créer les notifications
    private void createExamNotifications(Exam exam) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            LocalDateTime examDateTime = LocalDateTime.of(exam.getExamDate(), exam.getStartTime());

            Map<String, Object> notificationRequest = new HashMap<>();
            notificationRequest.put("examId", exam.getId());
            notificationRequest.put("examTitle", exam.getTitle());
            notificationRequest.put("courseName", "Cours"); // Tu peux améliorer en récupérant le nom du cours
            notificationRequest.put("examDateTime", examDateTime.toString());
            notificationRequest.put("startTime", exam.getStartTime().toString());
            notificationRequest.put("room", exam.getRoomId() != null ? "Salle " + exam.getRoomId() : "À confirmer");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notificationRequest, headers);

            // Appeler ms-auth pour créer les notifications
            restTemplate.postForEntity("http://localhost:8081/auth/notifications/exam", entity, String.class);

            System.out.println("✅ Notifications créées pour l'examen: " + exam.getTitle());

        } catch (Exception e) {
            System.err.println("⚠️ Erreur création notifications: " + e.getMessage());
        }
    }

    @Transactional
    public ExamResponse updateExam(Long id, UpdateExamRequest request) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new RuntimeException("Exam not found"));

        if (request.getTitle() != null) exam.setTitle(request.getTitle());
        if (request.getRoomId() != null) exam.setRoomId(request.getRoomId());
        if (request.getExamDate() != null) exam.setExamDate(request.getExamDate());
        if (request.getStartTime() != null) exam.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) exam.setEndTime(request.getEndTime());
        if (request.getStatus() != null) exam.setStatus(request.getStatus());
        if (request.getMaxScore() != null) exam.setMaxScore(request.getMaxScore());
        if (request.getPassingScore() != null) exam.setPassingScore(request.getPassingScore());
        if (request.getInstructions() != null) exam.setInstructions(request.getInstructions());

        examRepository.save(exam);
        return mapExamToResponse(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) throw new RuntimeException("Exam not found");
        examRepository.deleteById(id);
    }

    // Result methods
    public List<ExamResultResponse> getResultsByExam(Long examId) {
        return resultRepository.findByExamId(examId).stream().map(this::mapResultToResponse).collect(Collectors.toList());
    }

    public List<ExamResultResponse> getResultsByStudent(Long studentId) {
        return resultRepository.findByStudentId(studentId).stream().map(this::mapResultToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ExamResultResponse submitResult(CreateResultRequest request) {
        Exam exam = examRepository.findById(request.getExamId()).orElseThrow(() -> new RuntimeException("Exam not found"));

        ResultStatus status;
        if (!request.isPresent()) {
            status = ResultStatus.ABSENT;
        } else if (request.getScore() >= exam.getPassingScore()) {
            status = ResultStatus.PASSED;
        } else {
            status = ResultStatus.FAILED;
        }

        ExamResult result = ExamResult.builder()
                .examId(request.getExamId())
                .studentId(request.getStudentId())
                .score(request.getScore())
                .isPresent(request.isPresent())
                .status(status)
                .comments(request.getComments())
                .gradedBy(request.getGradedBy())
                .gradedAt(LocalDateTime.now())
                .build();

        resultRepository.save(result);
        return mapResultToResponse(result);
    }

    private ExamResponse mapExamToResponse(Exam e) {
        return ExamResponse.builder()
                .id(e.getId()).examCode(e.getExamCode()).title(e.getTitle()).courseId(e.getCourseId())
                .roomId(e.getRoomId()).examDate(e.getExamDate()).startTime(e.getStartTime())
                .endTime(e.getEndTime()).durationMinutes(e.getDurationMinutes()).type(e.getType())
                .status(e.getStatus()).maxScore(e.getMaxScore()).passingScore(e.getPassingScore())
                .instructions(e.getInstructions()).academicYear(e.getAcademicYear()).semester(e.getSemester())
                .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt()).build();
    }

    private ExamResultResponse mapResultToResponse(ExamResult r) {
        return ExamResultResponse.builder()
                .id(r.getId()).examId(r.getExamId()).studentId(r.getStudentId()).score(r.getScore())
                .isPresent(r.isPresent()).status(r.getStatus()).comments(r.getComments())
                .gradedBy(r.getGradedBy()).gradedAt(r.getGradedAt()).build();
    }
}