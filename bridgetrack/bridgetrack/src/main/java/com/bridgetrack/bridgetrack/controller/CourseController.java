package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.CourseRequest;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;

    public CourseController(CourseRepository courseRepository,
                            ProgramRepository programRepository) {
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
    }

    @GetMapping
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course addCourse(@RequestBody CourseRequest request) {
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setModality(request.getModality());
        course.setCategory(request.getCategory());
        course.setRequiresPrereq(request.getRequiresPrereq());
        course.setProgram(program);

        return courseRepository.save(course);
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}