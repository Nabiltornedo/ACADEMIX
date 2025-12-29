package com.academix.auth.service;

import com.academix.auth.entity.Notification;
import com.academix.auth.entity.NotificationType;
import com.academix.auth.entity.Role;
import com.academix.auth.entity.User;
import com.academix.auth.repository.NotificationRepository;
import com.academix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Vérifie toutes les 15 minutes
    @Scheduled(fixedRate = 900000)
    public void checkAndCreateNotifications() {
        log.info("=== Vérification des examens à venir ===");
        checkUpcomingExams();
    }

    // Exécuter au démarrage (après 10 secondes)
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void checkOnStartup() {
        log.info("=== Vérification initiale des examens au démarrage ===");
        checkUpcomingExams();
    }

    private void checkUpcomingExams() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String examServiceUrl = "http://localhost:8085/exams";

            List<Map<String, Object>> exams = restTemplate.getForObject(examServiceUrl, List.class);

            if (exams == null || exams.isEmpty()) {
                log.info("Aucun examen trouvé");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            List<User> students = userRepository.findByRole(Role.STUDENT);
            log.info("Trouvé {} étudiants et {} examens", students.size(), exams.size());

            for (Map<String, Object> exam : exams) {
                try {
                    String examDateStr = (String) exam.get("examDate");
                    if (examDateStr == null) continue;

                    LocalDate examDate = LocalDate.parse(examDateStr.substring(0, 10));
                    String examTitle = (String) exam.get("title");
                    Long examId = exam.get("id") != null ? Long.valueOf(exam.get("id").toString()) : null;

                    // Récupérer le nom du cours
                    String courseName = examTitle;
                    if (exam.get("courseName") != null) {
                        courseName = (String) exam.get("courseName");
                    }

                    // Examen demain - Notification 24h
                    if (examDate.equals(tomorrow)) {
                        log.info("Examen demain trouvé: {}", examTitle);
                        createNotificationsForAllStudents(
                                students,
                                "Examen demain : " + examTitle,
                                "Vous avez un examen de " + courseName + " demain. Bonne révision !",
                                NotificationType.EXAM_REMINDER_24H
                        );
                    }

                    // Examen aujourd'hui - Notification urgente
                    if (examDate.equals(today)) {
                        log.info("Examen aujourd'hui trouvé: {}", examTitle);
                        String startTime = exam.get("startTime") != null ? exam.get("startTime").toString() : "";
                        String room = exam.get("room") != null ? (String) exam.get("room") : "À confirmer";

                        createNotificationsForAllStudents(
                                students,
                                "URGENT - Examen aujourd'hui : " + examTitle,
                                "Votre examen de " + courseName + " a lieu aujourd'hui" +
                                        (startTime.isEmpty() ? "" : " à " + startTime) +
                                        ". Salle: " + room + ". Bonne chance !",
                                NotificationType.EXAM_REMINDER_2H
                        );
                    }

                } catch (Exception e) {
                    log.error("Erreur traitement exam: {}", e.getMessage());
                }
            }

            log.info("=== Vérification terminée ===");

        } catch (Exception e) {
            log.error("Erreur connexion au service exam: {}", e.getMessage());
        }
    }

    private void createNotificationsForAllStudents(List<User> students, String title, String message, NotificationType type) {
        for (User student : students) {
            try {
                // Vérifier si notification existe déjà
                boolean exists = notificationRepository.existsByUserIdAndTitle(student.getId(), title);

                if (!exists) {
                    Notification notification = Notification.builder()
                            .userId(student.getId())
                            .title(title)
                            .message(message)
                            .type(type)
                            .isRead(false)
                            .build();
                    notificationRepository.save(notification);
                    log.info("Notification créée pour: {} - {}", student.getUsername(), title);
                }
            } catch (Exception e) {
                log.error("Erreur création notification pour {}: {}", student.getUsername(), e.getMessage());
            }
        }
    }

    // Méthode appelée par ms-exam pour créer les rappels d'examen
    public void createExamNotifications(Long userId, Long examId, String examTitle,
                                        String courseName, LocalDateTime examDateTime, String room) {
        // Notification 24h avant
        LocalDateTime reminder24h = examDateTime.minusHours(24);
        if (reminder24h.isAfter(LocalDateTime.now())) {
            notificationService.createExamReminder24h(userId, examId, examTitle, courseName, examDateTime);
        }

        // Notification 2h avant
        LocalDateTime reminder2h = examDateTime.minusHours(2);
        if (reminder2h.isAfter(LocalDateTime.now())) {
            notificationService.createExamReminder2h(userId, examId, examTitle, courseName,
                    examDateTime, examDateTime.toLocalTime().toString(), room);
        }
    }

    // Méthode appelée par ms-schedule pour créer les rappels de cours
    public void createCourseNotifications(Long userId, Long scheduleId, String courseName,
                                          String dayOfWeek, LocalTime startTime, String room) {
        notificationService.createCourseReminder2h(userId, scheduleId, courseName,
                dayOfWeek, startTime.toString(), room);
    }
}