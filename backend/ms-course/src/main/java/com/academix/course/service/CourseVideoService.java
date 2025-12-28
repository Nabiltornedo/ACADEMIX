package com.academix.course.service;

import com.academix.course.dto.CourseVideoDtos.*;
import com.academix.course.entity.CourseVideo;
import com.academix.course.repository.CourseVideoRepository;
import com.academix.course.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseVideoService {

    private final CourseVideoRepository videoRepository;
    private final CourseRepository courseRepository;

    public CourseVideoService(CourseVideoRepository videoRepository, CourseRepository courseRepository) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
    }

    // Créer une vidéo
    @Transactional
    public VideoResponse createVideo(CreateVideoRequest request) {
        // Vérifier que le cours existe
        if (!courseRepository.existsById(request.getCourseId())) {
            throw new RuntimeException("Course not found with id: " + request.getCourseId());
        }

        // Définir l'ordre si non spécifié
        int order = request.getOrderIndex() != null ? request.getOrderIndex()
                : videoRepository.countByCourseId(request.getCourseId()) + 1;

        CourseVideo video = CourseVideo.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(request.getVideoUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .durationMinutes(request.getDurationMinutes())
                .orderIndex(order)
                .uploadedBy(request.getUploadedBy())
                .isPublished(true)
                .build();

        video = videoRepository.save(video);
        return mapToResponse(video);
    }

    // Obtenir toutes les vidéos d'un cours (pour admin/prof)
    public List<VideoResponse> getVideosByCourse(Long courseId) {
        return videoRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtenir les vidéos publiées d'un cours (pour étudiants)
    public List<VideoResponse> getPublishedVideosByCourse(Long courseId) {
        return videoRepository.findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtenir une vidéo par ID
    public VideoResponse getVideoById(Long id) {
        CourseVideo video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        return mapToResponse(video);
    }

    // Mettre à jour une vidéo
    @Transactional
    public VideoResponse updateVideo(Long id, UpdateVideoRequest request) {
        CourseVideo video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        if (request.getTitle() != null) video.setTitle(request.getTitle());
        if (request.getDescription() != null) video.setDescription(request.getDescription());
        if (request.getVideoUrl() != null) video.setVideoUrl(request.getVideoUrl());
        if (request.getThumbnailUrl() != null) video.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getDurationMinutes() != null) video.setDurationMinutes(request.getDurationMinutes());
        if (request.getOrderIndex() != null) video.setOrderIndex(request.getOrderIndex());
        if (request.getIsPublished() != null) video.setIsPublished(request.getIsPublished());

        video = videoRepository.save(video);
        return mapToResponse(video);
    }

    // Supprimer une vidéo
    @Transactional
    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
    }

    private VideoResponse mapToResponse(CourseVideo video) {
        return VideoResponse.builder()
                .id(video.getId())
                .courseId(video.getCourseId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .durationMinutes(video.getDurationMinutes())
                .orderIndex(video.getOrderIndex())
                .isPublished(video.getIsPublished())
                .uploadedBy(video.getUploadedBy())
                .createdAt(video.getCreatedAt() != null ? video.getCreatedAt().toString() : null)
                .updatedAt(video.getUpdatedAt() != null ? video.getUpdatedAt().toString() : null)
                .build();
    }
}
