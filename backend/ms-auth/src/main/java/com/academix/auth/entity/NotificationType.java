package com.academix.auth.entity;

public enum NotificationType {
    EXAM_REMINDER_24H,    // 24h avant exam
    EXAM_REMINDER_2H,     // 2h avant exam
    COURSE_REMINDER_2H,   // 2h avant cours
    INFO,                 // Information générale
    WARNING,              // Avertissement
    SUCCESS               // Succès
}