package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

   
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
       
        this.studentService = studentService;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentService.registerStudent(student);
    }

    @GetMapping
    public List<Student> list() {
        return studentService.getAllStudents();
    }
   
}
