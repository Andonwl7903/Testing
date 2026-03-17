package com.bridgetrack.bridgetrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bridgetrack.bridgetrack.repository.AdminUserRepository;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            AdminUserRepository adminRepo,
            StudentRepository studentRepo,
            InstructorRepository instructorRepo,
            PasswordEncoder passwordEncoder
    ) {
        return loginValue -> {

            if (loginValue.equals("instructor@test.com")) {
                return User.withUsername("instructor@test.com")
                        .password(passwordEncoder.encode("password123"))
                        .roles("INSTRUCTOR")
                        .build();
            }

            if (loginValue.equals("admin@test.com")) {
                return User.withUsername("admin@test.com")
                        .password(passwordEncoder.encode("password123"))
                        .roles("ADMIN")
                        .build();
            }

            if (loginValue.equals("student@test.com")) {
                return User.withUsername("student@test.com")
                        .password(passwordEncoder.encode("password123"))
                        .roles("STUDENT")
                        .build();
            }

            var admin = adminRepo.findByUsername(loginValue);
            if (admin.isPresent()) {
                var u = admin.get();
                return User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRole().replace("ROLE_", ""))
                        .build();
            }

            var instructor = instructorRepo.findByEmail(loginValue);
            if (instructor.isPresent()) {
                var u = instructor.get();
                return User.withUsername(u.getEmail())
                        .password(u.getPassword())
                        .roles(u.getRole().replace("ROLE_", ""))
                        .build();
            }

            var student = studentRepo.findByEmail(loginValue);
            if (student.isPresent()) {
                var u = student.get();
                return User.withUsername(u.getEmail())
                        .password(u.getPasswordHash())
                        .roles("STUDENT")
                        .build();
            }

            throw new UsernameNotFoundException("User not found: " + loginValue);
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            boolean isAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            boolean isInstructor = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

            boolean isStudent = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

            if (isAdmin) {
                response.sendRedirect("/admindashboard");
            } else if (isInstructor) {
                response.sendRedirect("/instructordashboard.html");
            } else if (isStudent) {
                response.sendRedirect("/studentdashboard.html");
            } else {
                response.sendRedirect("/bridgetrack-signin.html?error=true");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(
            	    	"/",
            	    	"/homepage",
            	        "/bridgetrack-signin.html", // your sign-in page
            	        "/login",                   // login processing
            	        "/css/**", "/js/**", "/images/**", "/webjars/**", "/bridgetrack.css", "/bridgetrack.js", "/favicon.ico"
            	    ).permitAll()
            	    .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/bridgetrack-signin.html")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/bridgetrack-signin.html?error=true")
                .permitAll()
            )

            .httpBasic(Customizer.withDefaults())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/bridgetrack-signin.html")
            );

        return http.build();
    }
}