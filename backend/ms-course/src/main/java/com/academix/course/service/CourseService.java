package com.academix.course.service;

import com.academix.course.dto.CourseDtos.*;
import com.academix.course.entity.*;
import com.academix.course.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    // Course methods
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapCourseToResponse).collect(Collectors.toList());
    }
    
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        return mapCourseToResponse(course);
    }
    
    public CourseResponse getCourseByCode(String code) {
        Course course = courseRepository.findByCourseCode(code).orElseThrow(() -> new RuntimeException("Course not found"));
        return mapCourseToResponse(course);
    }
    
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) throw new RuntimeException("Course code already exists");
        
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .name(request.getName())
                .description(request.getDescription())
                .credits(request.getCredits())
                .hoursPerWeek(request.getHoursPerWeek())
                .teacherId(request.getTeacherId())
                .programId(request.getProgramId())
                .semester(request.getSemester())
                .type(request.getType())
                .maxStudents(request.getMaxStudents())
                .status(CourseStatus.ACTIVE)
                .build();
        
        courseRepository.save(course);
        return mapCourseToResponse(course);
    }
    
    @Transactional
    public CourseResponse updateCourse(Long id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (request.getName() != null) course.setName(request.getName());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getCredits() != null) course.setCredits(request.getCredits());
        if (request.getHoursPerWeek() != null) course.setHoursPerWeek(request.getHoursPerWeek());
        if (request.getTeacherId() != null) course.setTeacherId(request.getTeacherId());
        if (request.getProgramId() != null) course.setProgramId(request.getProgramId());
        if (request.getSemester() != null) course.setSemester(request.getSemester());
        if (request.getType() != null) course.setType(request.getType());
        if (request.getStatus() != null) course.setStatus(request.getStatus());
        if (request.getMaxStudents() != null) course.setMaxStudents(request.getMaxStudents());
        
        courseRepository.save(course);
        return mapCourseToResponse(course);
    }
    
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) throw new RuntimeException("Course not found");
        courseRepository.deleteById(id);
    }
    
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream().map(this::mapCourseToResponse).collect(Collectors.toList());
    }
    
    public List<CourseResponse> getCoursesByProgram(Long programId) {
        return courseRepository.findByProgramId(programId).stream().map(this::mapCourseToResponse).collect(Collectors.toList());
    }
    
    // Program methods
    public List<ProgramResponse> getAllPrograms() {
        return programRepository.findAll().stream().map(this::mapProgramToResponse).collect(Collectors.toList());
    }
    
    public ProgramResponse getProgramById(Long id) {
        Program program = programRepository.findById(id).orElseThrow(() -> new RuntimeException("Program not found"));
        return mapProgramToResponse(program);
    }
    
    @Transactional
    public ProgramResponse createProgram(CreateProgramRequest request) {
        if (programRepository.existsByProgramCode(request.getProgramCode())) throw new RuntimeException("Program code already exists");
        
        Program program = Program.builder()
                .programCode(request.getProgramCode())
                .name(request.getName())
                .description(request.getDescription())
                .department(request.getDepartment())
                .durationYears(request.getDurationYears())
                .totalCredits(request.getTotalCredits())
                .level(request.getLevel())
                .isActive(true)
                .build();
        
        programRepository.save(program);
        return mapProgramToResponse(program);
    }
    
    @Transactional
    public void deleteProgram(Long id) {
        if (!programRepository.existsById(id)) throw new RuntimeException("Program not found");
        programRepository.deleteById(id);
    }
    
    // Enrollment methods
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::mapEnrollmentToResponse).collect(Collectors.toList());
    }
    
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream().map(this::mapEnrollmentToResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public EnrollmentResponse enrollStudent(CreateEnrollmentRequest request) {
        if (enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId()).isPresent()) {
            throw new RuntimeException("Student already enrolled in this course");
        }
        
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ENROLLED);
        if (course.getMaxStudents() != null && enrolledCount >= course.getMaxStudents()) {
            throw new RuntimeException("Course is full");
        }
        
        Enrollment enrollment = Enrollment.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .academicYear(request.getAcademicYear())
                .semester(request.getSemester())
                .status(EnrollmentStatus.ENROLLED)
                .build();
        
        enrollmentRepository.save(enrollment);
        return mapEnrollmentToResponse(enrollment);
    }
    
    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
    
    private CourseResponse mapCourseToResponse(Course course) {
        int enrolledCount = (int) enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ENROLLED);
        return CourseResponse.builder()
                .id(course.getId()).courseCode(course.getCourseCode()).name(course.getName())
                .description(course.getDescription()).credits(course.getCredits())
                .hoursPerWeek(course.getHoursPerWeek()).teacherId(course.getTeacherId())
                .programId(course.getProgramId()).semester(course.getSemester())
                .type(course.getType()).status(course.getStatus())
                .maxStudents(course.getMaxStudents()).enrolledCount(enrolledCount)
                .createdAt(course.getCreatedAt()).build();
    }
    
    private ProgramResponse mapProgramToResponse(Program program) {
        return ProgramResponse.builder()
                .id(program.getId()).programCode(program.getProgramCode()).name(program.getName())
                .description(program.getDescription()).department(program.getDepartment())
                .durationYears(program.getDurationYears()).totalCredits(program.getTotalCredits())
                .level(program.getLevel()).isActive(program.isActive()).createdAt(program.getCreatedAt()).build();
    }
    
    private EnrollmentResponse mapEnrollmentToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId()).studentId(enrollment.getStudentId()).courseId(enrollment.getCourseId())
                .academicYear(enrollment.getAcademicYear()).semester(enrollment.getSemester())
                .status(enrollment.getStatus()).enrollmentDate(enrollment.getEnrollmentDate()).build();
    }
}
