package com.academix.course.service;

import com.academix.course.entity.Course;
import com.academix.course.entity.CourseVideo;
import com.academix.course.repository.CourseRepository;
import com.academix.course.repository.CourseVideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AISummaryService {

    private final CourseRepository courseRepository;
    private final CourseVideoRepository videoRepository;
    private final RestTemplate restTemplate;

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    public AISummaryService(CourseRepository courseRepository, CourseVideoRepository videoRepository) {
        this.courseRepository = courseRepository;
        this.videoRepository = videoRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public String generateCourseSummary(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<CourseVideo> videos = videoRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // Construire le contexte du cours
        String courseContext = buildCourseContext(course, videos);

        // Générer le résumé avec l'IA
        String summary = callAI(courseContext);

        // Sauvegarder le résumé
        course.setAiSummary(summary);
        course.setSummaryGeneratedAt(LocalDateTime.now());
        courseRepository.save(course);

        return summary;
    }

    public String getCourseSummary(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getAiSummary();
    }

    public boolean hasSummary(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getAiSummary() != null && !course.getAiSummary().isEmpty();
    }

    private String buildCourseContext(Course course, List<CourseVideo> videos) {
        StringBuilder context = new StringBuilder();
        context.append("Cours: ").append(course.getName()).append("\n");
        if (course.getDescription() != null) {
            context.append("Description: ").append(course.getDescription()).append("\n");
        }
        if (course.getType() != null) {
            context.append("Type: ").append(course.getType()).append("\n");
        }
        if (course.getCredits() != null) {
            context.append("Crédits: ").append(course.getCredits()).append("\n");
        }
        context.append("\n");

        if (!videos.isEmpty()) {
            context.append("Vidéos du cours:\n");
            for (int i = 0; i < videos.size(); i++) {
                CourseVideo video = videos.get(i);
                context.append(i + 1).append(". ").append(video.getTitle()).append("\n");
                if (video.getDescription() != null && !video.getDescription().isEmpty()) {
                    context.append("   Description: ").append(video.getDescription()).append("\n");
                }
                if (video.getDurationMinutes() != null && video.getDurationMinutes() > 0) {
                    context.append("   Durée: ").append(video.getDurationMinutes()).append(" minutes\n");
                }
            }
        } else {
            context.append("Aucune vidéo disponible.\n");
        }

        return context.toString();
    }

    private String callAI(String courseContext) {
        // Si pas de clé API, générer un résumé basique
        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            return generateBasicSummary(courseContext);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "claude-3-haiku-20240307");
            requestBody.put("max_tokens", 1500);

            String prompt = "Tu es un assistant pédagogique expert. Génère un résumé complet et structuré de ce cours universitaire basé sur les informations suivantes. " +
                    "Le résumé doit aider les étudiants à comprendre les concepts clés du cours. " +
                    "Structure le résumé avec les sections suivantes:\n" +
                    "1) 🎯 Objectifs du cours\n" +
                    "2) 📚 Points clés à retenir\n" +
                    "3) 💡 Concepts importants\n" +
                    "4) 📝 Conseils d'apprentissage\n\n" +
                    "Informations du cours:\n" + courseContext;

            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.anthropic.com/v1/messages",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().get("content") != null) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (!content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }

            return generateBasicSummary(courseContext);
        } catch (Exception e) {
            System.err.println("Error calling AI API: " + e.getMessage());
            return generateBasicSummary(courseContext);
        }
    }

    private String generateBasicSummary(String courseContext) {
        String[] lines = courseContext.split("\n");
        StringBuilder summary = new StringBuilder();

        String courseName = "";
        String courseDescription = "";
        List<String> videoTitles = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("Cours:")) {
                courseName = line.replace("Cours:", "").trim();
            } else if (line.startsWith("Description:")) {
                courseDescription = line.replace("Description:", "").trim();
            } else if (line.matches("^\\d+\\..*")) {
                videoTitles.add(line.trim());
            }
        }

        summary.append("🎯 OBJECTIFS DU COURS\n");
        summary.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        summary.append("Ce cours \"").append(courseName).append("\" vise à fournir aux étudiants ");
        summary.append("une compréhension approfondie des concepts fondamentaux.\n\n");

        if (!courseDescription.isEmpty()) {
            summary.append("📖 DESCRIPTION\n");
            summary.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            summary.append(courseDescription).append("\n\n");
        }

        summary.append("📚 CONTENU DU COURS\n");
        summary.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        if (!videoTitles.isEmpty()) {
            summary.append("Le cours est divisé en ").append(videoTitles.size()).append(" partie(s):\n");
            for (String title : videoTitles) {
                summary.append("  • ").append(title).append("\n");
            }
        } else {
            summary.append("Le contenu vidéo sera bientôt disponible.\n");
        }
        summary.append("\n");

        summary.append("💡 CONSEILS D'APPRENTISSAGE\n");
        summary.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        summary.append("  • Regardez les vidéos dans l'ordre proposé\n");
        summary.append("  • Prenez des notes pendant le visionnage\n");
        summary.append("  • Pratiquez régulièrement les concepts appris\n");
        summary.append("  • N'hésitez pas à revoir les parties complexes\n");
        summary.append("  • Posez des questions à votre enseignant si nécessaire\n\n");

        summary.append("📅 Résumé généré automatiquement par ACADEMIX");

        return summary.toString();
    }
}