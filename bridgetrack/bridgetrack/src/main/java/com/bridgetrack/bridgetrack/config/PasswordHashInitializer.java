package com.bridgetrack.bridgetrack.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Configuration
public class PasswordHashInitializer {

    /**
     * Initializes passwordHash for all students that don't have one
     * This runs automatically on application startup, only once per student
     */
    @Bean
    public CommandLineRunner initializePasswordHashes(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                var studentsWithoutPassword = studentRepository.findAll()
                    .stream()
                    .filter(s -> s.getPasswordHash() == null || s.getPasswordHash().isBlank())
                    .toList();

                if (studentsWithoutPassword.isEmpty()) {
                    System.out.println("✓ All students have password hashes. No initialization needed.");
                    return;
                }

                System.out.println("Initializing password hashes for " + studentsWithoutPassword.size() + " students...");

                for (Student student : studentsWithoutPassword) {
                    // Generate a temporary password from their email
                    String tempPassword = generateTemporaryPassword(student.getEmail());
                    student.setPasswordHash(passwordEncoder.encode(tempPassword));
                    studentRepository.save(student);
                    System.out.println("  ✓ Initialized: " + student.getEmail() + " -> Password: " + tempPassword);
                }

                System.out.println("✓ Password initialization completed for " + studentsWithoutPassword.size() + " students");
                System.out.println("  Students can now sign in with their temporary passwords.");
            } catch (Exception e) {
                System.err.println("Error initializing password hashes: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Generates a temporary password from email
     * Examples: 
     *   john.doe@email.com -> John.Doe123!
     *   morgan.gonzalez190#example.org -> Morgan.Gonzalez190123!
     */
    private static String generateTemporaryPassword(String email) {
        if (email == null || email.isBlank()) {
            return "TempPassword123!";
        }

        // Extract the part before @ or #
        String localPart = email.split("[@#]")[0];

        // Split by dots and process each word
        String[] words = localPart.split("\\.");
        StringBuilder password = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Separate letters from numbers
                StringBuilder letters = new StringBuilder();
                StringBuilder numbers = new StringBuilder();
                
                for (char c : word.toCharArray()) {
                    if (Character.isLetter(c)) {
                        letters.append(c);
                    } else if (Character.isDigit(c)) {
                        numbers.append(c);
                    }
                }
                
                // Capitalize first letter and add the rest
                if (letters.length() > 0) {
                    password.append(letters.charAt(0))
                            .append(letters.substring(1).toLowerCase());
                }
                
                // Add any numbers from this word
                if (numbers.length() > 0) {
                    password.append(numbers);
                }
            }
        }

        // Add special character for complexity
        password.append("!");

        return password.toString();
    }
}


