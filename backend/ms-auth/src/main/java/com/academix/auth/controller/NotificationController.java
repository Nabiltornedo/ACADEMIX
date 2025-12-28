package com.academix.auth.controller;

import com.academix.auth.entity.Notification;
import com.academix.auth.entity.NotificationType;
import com.academix.auth.entity.Role;
import com.academix.auth.entity.User;
import com.academix.auth.repository.UserRepository;
import com.academix.auth.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // ============ ENDPOINTS UTILISATEUR ============

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@PathVariable Long userId) {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", notificationService.getUnreadCount(userId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // ============ NOTIFICATIONS AUTOMATIQUES EXAM ============

    @PostMapping("/exam")
    public ResponseEntity<Map<String, String>> createExamNotifications(@RequestBody Map<String, Object> request) {
        try {
            Long examId = Long.valueOf(request.get("examId").toString());
            String examTitle = (String) request.get("examTitle");
            String courseName = request.get("courseName") != null ? (String) request.get("courseName") : "Cours";
            String examDateTimeStr = (String) request.get("examDateTime");
            String startTime = request.get("startTime") != null ? (String) request.get("startTime") : "";
            String room = request.get("room") != null ? (String) request.get("room") : "À confirmer";

            LocalDateTime examDateTime = LocalDateTime.parse(examDateTimeStr);

            List<User> students = userRepository.findByRole(Role.STUDENT);
            int notifCount = 0;

            for (User student : students) {
                // Notification 24h avant
                if (examDateTime.minusHours(24).isAfter(LocalDateTime.now())) {
                    notificationService.createNotification(
                            student.getId(),
                            "📚 Rappel de révision - " + examTitle,
                            String.format("L'examen \"%s\" du cours %s aura lieu dans 24 heures.\n\n📅 Date: %s\n⏰ Heure: %s\n\n💡 Conseils:\n• Révisez les points clés du cours\n• Relisez vos notes\n• Reposez-vous bien cette nuit",
                                    examTitle, courseName, examDateTime.toLocalDate(), startTime),
                            NotificationType.EXAM_REMINDER_24H,
                            examId,
                            "EXAM",
                            examDateTime.minusHours(24)
                    );
                    notifCount++;
                }

                // Notification 2h avant
                if (examDateTime.minusHours(2).isAfter(LocalDateTime.now())) {
                    notificationService.createNotification(
                            student.getId(),
                            "⏰ Examen imminent - " + examTitle,
                            String.format("L'examen \"%s\" commence dans 2 heures!\n\n📅 Aujourd'hui\n⏰ Heure: %s\n📍 Salle: %s\n📖 Cours: %s\n\n✅ Checklist:\n• Carte d'étudiant\n• Stylos et matériel\n• Arrivez 15 min en avance",
                                    examTitle, startTime, room, courseName),
                            NotificationType.EXAM_REMINDER_2H,
                            examId,
                            "EXAM",
                            examDateTime.minusHours(2)
                    );
                    notifCount++;
                }
            }

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", notifCount + " notifications créées pour " + students.size() + " étudiants");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ============ NOTIFICATIONS AUTOMATIQUES COURS ============

    @PostMapping("/schedule")
    public ResponseEntity<Map<String, String>> createScheduleNotifications(@RequestBody Map<String, Object> request) {
        try {
            Long scheduleId = Long.valueOf(request.get("scheduleId").toString());
            String courseName = request.get("courseName") != null ? (String) request.get("courseName") : "Cours";
            String dayOfWeek = request.get("dayOfWeek") != null ? (String) request.get("dayOfWeek") : "";
            String startTime = request.get("startTime") != null ? (String) request.get("startTime") : "";
            String room = request.get("room") != null ? (String) request.get("room") : "À confirmer";
            String teacherName = request.get("teacherName") != null ? (String) request.get("teacherName") : "";

            List<User> students = userRepository.findByRole(Role.STUDENT);

            for (User student : students) {
                notificationService.createNotification(
                        student.getId(),
                        "📖 Cours dans 2 heures - " + courseName,
                        String.format("Le cours \"%s\" commence dans 2 heures.\n\n📅 Jour: %s\n⏰ Heure: %s\n📍 Salle: %s\n👨‍🏫 Enseignant: %s\n\n📝 N'oubliez pas vos affaires!",
                                courseName, dayOfWeek, startTime, room, teacherName),
                        NotificationType.COURSE_REMINDER_2H,
                        scheduleId,
                        "SCHEDULE",
                        LocalDateTime.now()
                );
            }

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Notifications créées pour " + students.size() + " étudiants");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ============ ENDPOINT DE TEST ============

    @PostMapping("/test/{userId}")
    public ResponseEntity<Map<String, String>> createTestNotifications(@PathVariable Long userId) {
        try {
            notificationService.createNotification(
                    userId,
                    "📚 Rappel de révision - Examen Marketing",
                    "L'examen \"Marketing Digital\" aura lieu dans 24 heures.\n\n📅 Date: Demain à 10:00\n📖 Cours: Marketing\n\n💡 Conseils:\n• Révisez les points clés\n• Relisez vos notes\n• Reposez-vous bien",
                    NotificationType.EXAM_REMINDER_24H,
                    100L,
                    "EXAM",
                    LocalDateTime.now()
            );

            notificationService.createNotification(
                    userId,
                    "⏰ Examen imminent - Algèbre",
                    "L'examen \"Algèbre Linéaire\" commence dans 2 heures!\n\n📅 Aujourd'hui à 14:00\n📍 Salle: Amphi A\n\n✅ N'oubliez pas:\n• Carte d'étudiant\n• Calculatrice",
                    NotificationType.EXAM_REMINDER_2H,
                    101L,
                    "EXAM",
                    LocalDateTime.now()
            );

            notificationService.createNotification(
                    userId,
                    "📖 Cours dans 2 heures - Java",
                    "Le cours \"Programmation Java\" commence dans 2 heures.\n\n⏰ Heure: 14:00\n📍 Salle: B205\n\nN'oubliez pas votre laptop!",
                    NotificationType.COURSE_REMINDER_2H,
                    102L,
                    "SCHEDULE",
                    LocalDateTime.now()
            );

            notificationService.createNotification(
                    userId,
                    "ℹ️ Bienvenue sur ACADEMIX",
                    "Vous recevrez des notifications pour vos examens et cours. Bonne étude! 🎓",
                    NotificationType.INFO,
                    null,
                    null,
                    LocalDateTime.now()
            );

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "4 notifications de test créées!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
