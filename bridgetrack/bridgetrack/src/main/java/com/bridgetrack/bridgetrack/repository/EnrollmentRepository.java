package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentStudentId(Long studentId);

    Optional<Enrollment> findByStudentStudentIdAndSectionSectionId(Long studentId, Long sectionId);
}