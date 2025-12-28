package com.academix.course.repository;

import com.academix.course.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByProgramCode(String programCode);
    List<Program> findByDepartment(String department);
    List<Program> findByIsActive(boolean isActive);
    boolean existsByProgramCode(String programCode);
}
