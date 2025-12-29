package com.academix.auth.entity;

public enum NotificationType {
    EXAM_REMINDER_24H,    // 24h avant exam
    EXAM_REMINDER_2H,     // 2h avant exam
    COURSE_REMINDER_2H,   // 2h avant cours
    EXAM_REMINDER,        // NOUVEAU - Rappel exam général
    COURSE_REMINDER,      // NOUVEAU - Rappel cours général
    INFO,                 // Information générale
    WARNING,              // Avertissement
    SUCCESS               // Succès
}