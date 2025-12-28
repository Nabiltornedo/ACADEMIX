package com.academix.admin.controller;

import com.academix.admin.dto.AdminDtos.*;
import com.academix.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    
    // Settings endpoints
    @GetMapping("/settings")
    public ResponseEntity<List<SettingResponse>> getAllSettings() { return ResponseEntity.ok(adminService.getAllSettings()); }
    
    @GetMapping("/settings/{key}")
    public ResponseEntity<SettingResponse> getSettingByKey(@PathVariable String key) { return ResponseEntity.ok(adminService.getSettingByKey(key)); }
    
    @GetMapping("/settings/public")
    public ResponseEntity<List<SettingResponse>> getPublicSettings() { return ResponseEntity.ok(adminService.getPublicSettings()); }
    
    @PostMapping("/settings")
    public ResponseEntity<SettingResponse> createSetting(@RequestBody CreateSettingRequest request) { return ResponseEntity.ok(adminService.createSetting(request)); }
    
    @PutMapping("/settings/{key}")
    public ResponseEntity<SettingResponse> updateSetting(@PathVariable String key, @RequestBody UpdateSettingRequest request) { return ResponseEntity.ok(adminService.updateSetting(key, request)); }
    
    @DeleteMapping("/settings/{key}")
    public ResponseEntity<Void> deleteSetting(@PathVariable String key) { adminService.deleteSetting(key); return ResponseEntity.noContent().build(); }
    
    // Audit log endpoints
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getRecentAuditLogs() { return ResponseEntity.ok(adminService.getRecentAuditLogs()); }
    
    @GetMapping("/audit-logs/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUser(@PathVariable Long userId) { return ResponseEntity.ok(adminService.getAuditLogsByUser(userId)); }
    
    // Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() { return ResponseEntity.ok(adminService.getDashboardStats()); }
}
