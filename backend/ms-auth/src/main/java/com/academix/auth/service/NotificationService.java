package com.academix.auth.service;

import com.academix.auth.entity.Notification;
import com.academix.auth.entity.NotificationType;
import com.academix.auth.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Créer une notification
    @Transactional
    public Notification createNotification(Long userId, String title, String message,
                                           NotificationType type, Long referenceId,
                                           String referenceType, LocalDateTime scheduledFor) {

        // Vérifier si notification existe déjà
        if (referenceId != null && referenceType != null) {
            boolean exists = notificationRepository.existsByReferenceIdAndReferenceTypeAndType(
                    referenceId, referenceType, type);
            if (exists) return null;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .scheduledFor(scheduledFor != null ? scheduledFor : LocalDateTime.now())
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    // Obtenir toutes les notifications d'un utilisateur
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Obtenir les notifications non lues
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // Compter les notifications non lues
    public int getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Marquer comme lu
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    // Marquer toutes comme lues
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    // Supprimer une notification
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Créer notification pour exam (24h avant)
    public void createExamReminder24h(Long userId, Long examId, String examTitle, String courseName, LocalDateTime examDate) {
        String title = "📚 Rappel de révision - " + examTitle;
        String message = String.format(
                "L'examen \"%s\" du cours %s aura lieu dans 24 heures.\n\n" +
                        "📅 Date: %s\n\n" +
                        "💡 Conseils:\n" +
                        "• Révisez les points clés du cours\n" +
                        "• Relisez vos notes\n" +
                        "• Reposez-vous bien cette nuit",
                examTitle, courseName, examDate.toLocalDate()
        );

        createNotification(userId, title, message, NotificationType.EXAM_REMINDER_24H,
                examId, "EXAM", examDate.minusHours(24));
    }

    // Créer notification pour exam (2h avant)
    public void createExamReminder2h(Long userId, Long examId, String examTitle, String courseName,
                                     LocalDateTime examDate, String startTime, String room) {
        String title = "⏰ Examen imminent - " + examTitle;
        String message = String.format(
                "L'examen \"%s\" commence dans 2 heures!\n\n" +
                        "📅 Aujourd'hui à %s\n" +
                        "📍 Salle: %s\n" +
                        "📖 Cours: %s\n\n" +
                        "✅ Checklist:\n" +
                        "• Carte d'étudiant\n" +
                        "• Stylos et matériel\n" +
                        "• Arrivez 15 min en avance",
                examTitle, startTime, room != null ? room : "À confirmer", courseName
        );

        createNotification(userId, title, message, NotificationType.EXAM_REMINDER_2H,
                examId, "EXAM", examDate.minusHours(2));
    }

    // Créer notification pour cours (2h avant)
    public void createCourseReminder2h(Long userId, Long scheduleId, String courseName,
                                       String dayOfWeek, String startTime, String room) {
        String title = "📖 Cours dans 2 heures - " + courseName;
        String message = String.format(
                "Le cours \"%s\" commence dans 2 heures.\n\n" +
                        "⏰ Horaire: %s\n" +
                        "📍 Salle: %s\n\n" +
                        "N'oubliez pas vos affaires!",
                courseName, startTime, room != null ? room : "À confirmer"
        );

        createNotification(userId, title, message, NotificationType.COURSE_REMINDER_2H,
                scheduleId, "SCHEDULE", null);
    }
}