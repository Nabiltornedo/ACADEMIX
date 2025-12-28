package com.academix.admin.service;

import com.academix.admin.dto.AdminDtos.*;
import com.academix.admin.entity.*;
import com.academix.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SystemSettingRepository settingRepository;
    private final AuditLogRepository auditLogRepository;
    
    // Settings methods
    public List<SettingResponse> getAllSettings() {
        return settingRepository.findAll().stream().map(this::mapSettingToResponse).collect(Collectors.toList());
    }
    
    public SettingResponse getSettingByKey(String key) {
        SystemSetting setting = settingRepository.findBySettingKey(key).orElseThrow(() -> new RuntimeException("Setting not found"));
        return mapSettingToResponse(setting);
    }
    
    public List<SettingResponse> getPublicSettings() {
        return settingRepository.findByIsPublic(true).stream().map(this::mapSettingToResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public SettingResponse createSetting(CreateSettingRequest request) {
        if (settingRepository.existsBySettingKey(request.getSettingKey())) throw new RuntimeException("Setting key already exists");
        
        SystemSetting setting = SystemSetting.builder()
                .settingKey(request.getSettingKey())
                .settingValue(request.getSettingValue())
                .description(request.getDescription())
                .settingType(request.getSettingType())
                .isPublic(request.isPublic())
                .build();
        
        settingRepository.save(setting);
        return mapSettingToResponse(setting);
    }
    
    @Transactional
    public SettingResponse updateSetting(String key, UpdateSettingRequest request) {
        SystemSetting setting = settingRepository.findBySettingKey(key).orElseThrow(() -> new RuntimeException("Setting not found"));
        
        if (request.getSettingValue() != null) setting.setSettingValue(request.getSettingValue());
        if (request.getDescription() != null) setting.setDescription(request.getDescription());
        setting.setPublic(request.isPublic());
        setting.setUpdatedBy(request.getUpdatedBy());
        
        settingRepository.save(setting);
        return mapSettingToResponse(setting);
    }
    
    @Transactional
    public void deleteSetting(String key) {
        SystemSetting setting = settingRepository.findBySettingKey(key).orElseThrow(() -> new RuntimeException("Setting not found"));
        settingRepository.delete(setting);
    }
    
    // Audit log methods
    public List<AuditLogResponse> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc().stream().map(this::mapAuditLogToResponse).collect(Collectors.toList());
    }
    
    public List<AuditLogResponse> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId).stream().map(this::mapAuditLogToResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public void logAction(Long userId, String username, String action, String entityType, Long entityId, String details, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }
    
    // Dashboard stats - would normally call other services
    public DashboardStats getDashboardStats() {
        return DashboardStats.builder()
                .totalStudents(0)
                .totalTeachers(0)
                .totalCourses(0)
                .activeExams(0)
                .recentLogins(auditLogRepository.findByAction("LOGIN").size())
                .build();
    }
    
    private SettingResponse mapSettingToResponse(SystemSetting s) {
        return SettingResponse.builder()
                .id(s.getId()).settingKey(s.getSettingKey()).settingValue(s.getSettingValue())
                .description(s.getDescription()).settingType(s.getSettingType())
                .isPublic(s.isPublic()).updatedBy(s.getUpdatedBy()).updatedAt(s.getUpdatedAt()).build();
    }
    
    private AuditLogResponse mapAuditLogToResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId()).userId(a.getUserId()).username(a.getUsername()).action(a.getAction())
                .entityType(a.getEntityType()).entityId(a.getEntityId()).details(a.getDetails())
                .ipAddress(a.getIpAddress()).createdAt(a.getCreatedAt()).build();
    }
}
