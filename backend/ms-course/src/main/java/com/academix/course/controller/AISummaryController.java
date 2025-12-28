package com.academix.course.controller;

import com.academix.course.service.AISummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/courses/ai")
@RequiredArgsConstructor
public class AISummaryController {

    private final AISummaryService aiSummaryService;

    // Générer un résumé IA (Admin/Prof)
    @PostMapping("/generate-summary/{courseId}")
    public ResponseEntity<Map<String, String>> generateSummary(@PathVariable Long courseId) {
        try {
            String summary = aiSummaryService.generateCourseSummary(courseId);
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("summary", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Obtenir le résumé (Tous)
    @GetMapping("/summary/{courseId}")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long courseId) {
        try {
            String summary = aiSummaryService.getCourseSummary(courseId);
            boolean hasSummary = summary != null && !summary.isEmpty();

            Map<String, Object> response = new HashMap<>();
            response.put("courseId", courseId);
            response.put("summary", summary != null ? summary : "");
            response.put("hasSummary", hasSummary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}