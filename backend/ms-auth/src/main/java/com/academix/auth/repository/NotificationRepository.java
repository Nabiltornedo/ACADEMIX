package com.academix.auth.repository;

import com.academix.auth.entity.Notification;
import com.academix.auth.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    int countByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.scheduledFor <= :now AND n.isRead = false")
    List<Notification> findPendingNotifications(@Param("now") LocalDateTime now);

    boolean existsByReferenceIdAndReferenceTypeAndType(Long referenceId, String referenceType, NotificationType type);
}