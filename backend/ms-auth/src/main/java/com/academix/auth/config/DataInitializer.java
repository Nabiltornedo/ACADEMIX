package com.academix.auth.config;

import com.academix.auth.entity.Role;
import com.academix.auth.entity.User;
import com.academix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@academix.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("System")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin / admin123");
        }
        
        // Create default teacher if not exists
        if (!userRepository.existsByUsername("teacher")) {
            User teacher = User.builder()
                    .username("teacher")
                    .email("teacher@academix.com")
                    .password(passwordEncoder.encode("teacher123"))
                    .firstName("John")
                    .lastName("Doe")
                    .role(Role.TEACHER)
                    .isActive(true)
                    .build();
            userRepository.save(teacher);
            log.info("Default teacher user created: teacher / teacher123");
        }
        
        // Create default student if not exists
        if (!userRepository.existsByUsername("student")) {
            User student = User.builder()
                    .username("student")
                    .email("student@academix.com")
                    .password(passwordEncoder.encode("student123"))
                    .firstName("Jane")
                    .lastName("Smith")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .build();
            userRepository.save(student);
            log.info("Default student user created: student / student123");
        }
    }
}
