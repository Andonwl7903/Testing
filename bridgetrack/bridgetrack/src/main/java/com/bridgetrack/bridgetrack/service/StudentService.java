package com.bridgetrack.bridgetrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.exception.DuplicateStudentException;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student registerStudent(Student student) {

        
        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
                throw new DuplicateStudentException(
                    "Student already registered with this email."
                );
            }
        }

        
        if (student.getPhone() != null
                && !student.getPhone().isBlank()
                && student.getBirthDate() != null) {

            if (studentRepository.existsByPhoneAndBirthDate(
                    student.getPhone(),
                    student.getBirthDate())) {

                throw new DuplicateStudentException(
                    "Student already registered with this phone and birthdate."
                );
            }
        }

        return studentRepository.save(student);
    }
    
    public List<Student> getAllStudents() {
    	return studentRepository.findAll();
    	}
}
