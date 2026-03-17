package com.bridgetrack.bridgetrack.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.CreateStudentAccountRequest;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@RestController
@RequestMapping("/api/students")
public class RegistrationApiController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationApiController(StudentRepository studentRepository,
                                         PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody CreateStudentAccountRequest request) {

        if (request.getFirstname() == null || request.getFirstname().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "First name is required."));
        }

        if (request.getLastname() == null || request.getLastname().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Last name is required."));
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required."));
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Phone is required."));
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required."));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match."));
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "An account with that email already exists."));
        }

        Student student = new Student();
        student.setFirstName(request.getFirstname());
        student.setLastName(request.getLastname());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setUsername(request.getEmail());
        student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        student.setStatus("ACTIVE");
        student.setRegistered_At(LocalDate.now());
        student.setPossible_Duplicate(false);

        if (request.getDob() != null && !request.getDob().isBlank()) {
            student.setBirthDate(LocalDate.parse(request.getDob()));
            
            System.out.println("REGISTER ENDPOINT HIT: " + request.getEmail());
        }

        studentRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Account created successfully."));
    }
}