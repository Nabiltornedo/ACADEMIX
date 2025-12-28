package com.academix.student.service;

import com.academix.student.dto.StudentDtos.*;
import com.academix.student.entity.Student;
import com.academix.student.entity.StudentStatus;
import com.academix.student.repository.GradeRepository;
import com.academix.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return mapToResponse(student);
    }
    
    public StudentResponse getStudentByCode(String code) {
        Student student = studentRepository.findByStudentCode(code)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return mapToResponse(student);
    }
    
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        String studentCode = generateStudentCode();
        
        Student student = Student.builder()
                .studentCode(studentCode)
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .programId(request.getProgramId())
                .enrollmentDate(LocalDate.now())
                .status(StudentStatus.ACTIVE)
                .currentSemester(1)
                .build();
        
        studentRepository.save(student);
        return mapToResponse(student);
    }
    
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) student.setGender(request.getGender());
        if (request.getProgramId() != null) student.setProgramId(request.getProgramId());
        if (request.getStatus() != null) student.setStatus(request.getStatus());
        if (request.getCurrentSemester() != null) student.setCurrentSemester(request.getCurrentSemester());
        
        studentRepository.save(student);
        return mapToResponse(student);
    }
    
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found");
        }
        studentRepository.deleteById(id);
    }
    
    public List<StudentResponse> getStudentsByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<StudentResponse> getStudentsByProgram(Long programId) {
        return studentRepository.findByProgramId(programId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private String generateStudentCode() {
        String year = String.valueOf(LocalDate.now().getYear());
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "STU" + year + random;
    }
    
    private StudentResponse mapToResponse(Student student) {
        Double gpa = gradeRepository.calculateAverageByStudentId(student.getId());
        
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .userId(student.getUserId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .address(student.getAddress())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .programId(student.getProgramId())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .currentSemester(student.getCurrentSemester())
                .gpa(gpa)
                .createdAt(student.getCreatedAt())
                .build();
    }
}
