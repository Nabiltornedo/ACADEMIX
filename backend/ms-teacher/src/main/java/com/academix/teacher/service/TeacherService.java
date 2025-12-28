package com.academix.teacher.service;

import com.academix.teacher.dto.TeacherDtos.*;
import com.academix.teacher.entity.Availability;
import com.academix.teacher.entity.Teacher;
import com.academix.teacher.entity.TeacherStatus;
import com.academix.teacher.repository.AvailabilityRepository;
import com.academix.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final AvailabilityRepository availabilityRepository;
    
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        return mapToResponse(teacher);
    }
    
    public TeacherResponse getTeacherByCode(String code) {
        Teacher teacher = teacherRepository.findByTeacherCode(code).orElseThrow(() -> new RuntimeException("Teacher not found"));
        return mapToResponse(teacher);
    }
    
    @Transactional
    public TeacherResponse createTeacher(CreateTeacherRequest request) {
        if (teacherRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already exists");
        
        String teacherCode = "TCH" + LocalDate.now().getYear() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Teacher teacher = Teacher.builder()
                .teacherCode(teacherCode)
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .department(request.getDepartment())
                .specialization(request.getSpecialization())
                .officeLocation(request.getOfficeLocation())
                .hireDate(LocalDate.now())
                .status(TeacherStatus.ACTIVE)
                .build();
        
        teacherRepository.save(teacher);
        return mapToResponse(teacher);
    }
    
    @Transactional
    public TeacherResponse updateTeacher(Long id, UpdateTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        if (request.getFirstName() != null) teacher.setFirstName(request.getFirstName());
        if (request.getLastName() != null) teacher.setLastName(request.getLastName());
        if (request.getEmail() != null) teacher.setEmail(request.getEmail());
        if (request.getPhone() != null) teacher.setPhone(request.getPhone());
        if (request.getAddress() != null) teacher.setAddress(request.getAddress());
        if (request.getDepartment() != null) teacher.setDepartment(request.getDepartment());
        if (request.getSpecialization() != null) teacher.setSpecialization(request.getSpecialization());
        if (request.getOfficeLocation() != null) teacher.setOfficeLocation(request.getOfficeLocation());
        if (request.getStatus() != null) teacher.setStatus(request.getStatus());
        
        teacherRepository.save(teacher);
        return mapToResponse(teacher);
    }
    
    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) throw new RuntimeException("Teacher not found");
        availabilityRepository.deleteByTeacherId(id);
        teacherRepository.deleteById(id);
    }
    
    public List<TeacherResponse> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<TeacherResponse> getTeachersByStatus(TeacherStatus status) {
        return teacherRepository.findByStatus(status).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    // Availability methods
    public List<AvailabilityResponse> getTeacherAvailabilities(Long teacherId) {
        return availabilityRepository.findByTeacherId(teacherId).stream().map(this::mapAvailabilityToResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public AvailabilityResponse addAvailability(CreateAvailabilityRequest request) {
        Availability availability = Availability.builder()
                .teacherId(request.getTeacherId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isAvailable(request.isAvailable())
                .build();
        availabilityRepository.save(availability);
        return mapAvailabilityToResponse(availability);
    }
    
    @Transactional
    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
    
    private TeacherResponse mapToResponse(Teacher teacher) {
        List<AvailabilityResponse> availabilities = availabilityRepository.findByTeacherId(teacher.getId())
                .stream().map(this::mapAvailabilityToResponse).collect(Collectors.toList());
        
        return TeacherResponse.builder()
                .id(teacher.getId())
                .teacherCode(teacher.getTeacherCode())
                .userId(teacher.getUserId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .dateOfBirth(teacher.getDateOfBirth())
                .department(teacher.getDepartment())
                .specialization(teacher.getSpecialization())
                .hireDate(teacher.getHireDate())
                .status(teacher.getStatus())
                .officeLocation(teacher.getOfficeLocation())
                .createdAt(teacher.getCreatedAt())
                .availabilities(availabilities)
                .build();
    }
    
    private AvailabilityResponse mapAvailabilityToResponse(Availability a) {
        return AvailabilityResponse.builder()
                .id(a.getId()).teacherId(a.getTeacherId()).dayOfWeek(a.getDayOfWeek())
                .startTime(a.getStartTime()).endTime(a.getEndTime()).isAvailable(a.isAvailable())
                .build();
    }
}
