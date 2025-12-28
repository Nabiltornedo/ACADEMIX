package com.academix.course.controller;

import com.academix.course.dto.CourseVideoDtos.*;
import com.academix.course.service.CourseVideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses/videos")
public class CourseVideoController {

    private final CourseVideoService videoService;

    public CourseVideoController(CourseVideoService videoService) {
        this.videoService = videoService;
    }

    // Créer une vidéo (Admin/Prof)
    @PostMapping
    public ResponseEntity<VideoResponse> createVideo(@RequestBody CreateVideoRequest request) {
        return ResponseEntity.ok(videoService.createVideo(request));
    }

    // Obtenir toutes les vidéos d'un cours (Admin/Prof)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<VideoResponse>> getVideosByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(videoService.getVideosByCourse(courseId));
    }

    // Obtenir les vidéos publiées d'un cours (Étudiants)
    @GetMapping("/course/{courseId}/published")
    public ResponseEntity<List<VideoResponse>> getPublishedVideos(@PathVariable Long courseId) {
        return ResponseEntity.ok(videoService.getPublishedVideosByCourse(courseId));
    }

    // Obtenir une vidéo par ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    // Mettre à jour une vidéo (Admin/Prof)
    @PutMapping("/{id}")
    public ResponseEntity<VideoResponse> updateVideo(@PathVariable Long id, @RequestBody UpdateVideoRequest request) {
        return ResponseEntity.ok(videoService.updateVideo(id, request));
    }

    // Supprimer une vidéo (Admin/Prof)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
