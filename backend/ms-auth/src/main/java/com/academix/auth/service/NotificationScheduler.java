package com.academix.auth.service;

import com.academix.auth.entity.NotificationType;
import com.academix.auth.entity.User;
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
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Vérifie toutes les 15 minutes
    @Scheduled(fixedRate = 900000) // 15 min
    public void checkAndCreateNotifications() {
        log.info("Vérification des notifications...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in24Hours = now.plusHours(24);
        LocalDateTime in2Hours = now.plusHours(2);

        // Obtenir tous les étudiants
        List<User> students = userRepository.findByRole(com.academix.auth.entity.Role.STUDENT);

        for (User student : students) {
            // Les notifications seront créées par les microservices exam et schedule
            // via des appels API ou événements
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