package com.bridgetrack.bridgetrack.repository;

import java.time.LocalDate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
 
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneAndBirthDate(String phone, LocalDate birthDate);
    
    Optional<Student> findByUsername(String username);
    
    Optional<Student> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    
    long countByStatusIgnoreCase(String status);
}
