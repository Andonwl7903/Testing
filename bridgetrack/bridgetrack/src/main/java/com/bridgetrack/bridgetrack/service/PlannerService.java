package com.bridgetrack.bridgetrack.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.PlannerCourseDto;
import com.bridgetrack.bridgetrack.dto.PlannerEnrollmentDto;
import com.bridgetrack.bridgetrack.dto.PlannerEnrollmentRequestDto;
import com.bridgetrack.bridgetrack.dto.PlannerPageDto;
import com.bridgetrack.bridgetrack.dto.PlannerResponseDto;
import com.bridgetrack.bridgetrack.dto.PlannerSectionDto;
import com.bridgetrack.bridgetrack.dto.PlannerStudentDto;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
@Transactional
public class PlannerService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public PlannerService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            SectionRepository sectionRepository,
            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public PlannerPageDto getPlannerPage(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        PlannerStudentDto studentDto = getPlannerStudent(student.getStudentId());
        List<PlannerCourseDto> courses = getCourses();
        List<PlannerSectionDto> sections = getSections();
        PlannerResponseDto planner = getPlanner(student.getStudentId());

        PlannerPageDto dto = new PlannerPageDto();
        dto.setStudent(studentDto);
        dto.setCourses(courses);
        dto.setSections(sections);
        dto.setTerm1(planner.getTerm1());
        dto.setTerm2(planner.getTerm2());

        return dto;
    }

    public void resetPlanner(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(student.getStudentId());
        enrollmentRepository.deleteAll(enrollments);
    }

    public void addEnrollmentForLoggedInStudent(String username, PlannerEnrollmentRequestDto request) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        request.setStudentId(student.getStudentId());
        addEnrollment(student.getStudentId(), request);
    }

    public void removeEnrollmentForLoggedInStudent(String username, Long sectionId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        removeEnrollment(student.getStudentId(), sectionId);
    }

    public PlannerStudentDto getPlannerStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        PlannerStudentDto dto = new PlannerStudentDto();
        dto.setId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setCompletedCourses(new ArrayList<>());

        return dto;
    }

    public List<PlannerCourseDto> getCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapCourseToDto)
                .collect(Collectors.toList());
    }

    public List<PlannerSectionDto> getSections() {
        return sectionRepository.findAll()
                .stream()
                .map(this::mapSectionToDto)
                .collect(Collectors.toList());
    }

    public PlannerResponseDto getPlanner(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

        List<PlannerEnrollmentDto> term1 = enrollments.stream()
                .filter(e -> "Term 1".equalsIgnoreCase(e.getPlannedTerm()))
                .map(this::mapEnrollmentToDto)
                .collect(Collectors.toList());

        List<PlannerEnrollmentDto> term2 = enrollments.stream()
                .filter(e -> "Term 2".equalsIgnoreCase(e.getPlannedTerm()))
                .map(this::mapEnrollmentToDto)
                .collect(Collectors.toList());

        PlannerResponseDto response = new PlannerResponseDto();
        response.setStudentId(studentId);
        response.setTerm1(term1);
        response.setTerm2(term2);

        return response;
    }

    public void addEnrollment(Long studentId, PlannerEnrollmentRequestDto request) {
        if (request == null) {
            throw new RuntimeException("Enrollment request is required.");
        }

        if (request.getStudentId() == null || !studentId.equals(request.getStudentId())) {
            throw new RuntimeException("Student ID mismatch.");
        }

        if (request.getSectionId() == null) {
            throw new RuntimeException("Section ID is required.");
        }

        if (request.getPlannedTerm() == null || request.getPlannedTerm().isBlank()) {
            throw new RuntimeException("Term is required.");
        }

        String requestedTerm = request.getPlannedTerm().trim();
        if (!"Term 1".equalsIgnoreCase(requestedTerm) && !"Term 2".equalsIgnoreCase(requestedTerm)) {
            throw new RuntimeException("Invalid term.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found."));

        Course course = section.getCourse();
        if (course == null) {
            throw new RuntimeException("Section is missing course information.");
        }

        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentStudentId(studentId);

        boolean duplicateSection = existingEnrollments.stream()
                .anyMatch(e -> e.getSection() != null
                        && e.getSection().getSectionId() != null
                        && e.getSection().getSectionId().equals(section.getSectionId()));

        if (duplicateSection) {
            throw new RuntimeException("Student is already enrolled in this section.");
        }

        boolean duplicateCourse = existingEnrollments.stream()
                .anyMatch(e -> e.getSection() != null
                        && e.getSection().getCourse() != null
                        && e.getSection().getCourse().getCourseCode() != null
                        && course.getCourseCode() != null
                        && e.getSection().getCourse().getCourseCode().equalsIgnoreCase(course.getCourseCode()));

        if (duplicateCourse) {
            throw new RuntimeException("Student is already enrolled or planned for this course.");
        }

        List<Enrollment> sameTermEnrollments = existingEnrollments.stream()
                .filter(e -> requestedTerm.equalsIgnoreCase(e.getPlannedTerm()))
                .collect(Collectors.toList());

        for (Enrollment existing : sameTermEnrollments) {
            Section existingSection = existing.getSection();
            if (existingSection != null
                    && schedulesConflict(section.getMeeting_days_times(), existingSection.getMeeting_days_times())) {
                throw new RuntimeException("Schedule conflict with another selected section in this term.");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setPlannedTerm(requestedTerm);
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    public void removeEnrollment(Long studentId, Long sectionId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found."));

        Optional<Enrollment> enrollmentOpt =
                enrollmentRepository.findByStudentStudentIdAndSectionSectionId(studentId, sectionId);

        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        String removedTerm = enrollment.getPlannedTerm();

        enrollmentRepository.delete(enrollment);

        if ("Term 1".equalsIgnoreCase(removedTerm)) {
            List<Enrollment> remaining = enrollmentRepository.findByStudentStudentId(studentId);
            boolean hasAnyTerm1 = remaining.stream()
                    .anyMatch(e -> "Term 1".equalsIgnoreCase(e.getPlannedTerm()));

            if (!hasAnyTerm1) {
                List<Enrollment> term2Enrollments = remaining.stream()
                        .filter(e -> "Term 2".equalsIgnoreCase(e.getPlannedTerm()))
                        .collect(Collectors.toList());

                enrollmentRepository.deleteAll(term2Enrollments);
            }
        }
    }

    private PlannerCourseDto mapCourseToDto(Course course) {
        PlannerCourseDto dto = new PlannerCourseDto();
        dto.setId(course.getCourseId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(3);

        if (course.getProgram() != null) {
            dto.setProgramName(course.getProgram().getProgramName());
        } else {
            dto.setProgramName("General");
        }

        dto.setPrerequisites(new ArrayList<>());
        return dto;
    }

    private PlannerSectionDto mapSectionToDto(Section section) {
        PlannerSectionDto dto = new PlannerSectionDto();
        dto.setId(section.getSectionId());
        dto.setSectionCode(buildSectionCode(section));
        dto.setSchedule(section.getMeeting_days_times());
        dto.setModality(section.getModality());
        dto.setTerm(resolveTermName(section));

        if (section.getCourse() != null) {
            dto.setCourseId(section.getCourse().getCourseId());
            dto.setCourseName(section.getCourse().getCourseName());
        }

        dto.setInstructorName(section.getInstructorId() != null && !section.getInstructorId().isBlank()
                ? section.getInstructorId()
                : "TBD");

        return dto;
    }

    private PlannerEnrollmentDto mapEnrollmentToDto(Enrollment enrollment) {
        PlannerEnrollmentDto dto = new PlannerEnrollmentDto();

        Section section = enrollment.getSection();
        Course course = section != null ? section.getCourse() : null;

        if (section != null) {
            dto.setSectionId(section.getSectionId());
            dto.setSectionCode(buildSectionCode(section));
            dto.setSchedule(section.getMeeting_days_times());
            dto.setModality(section.getModality());
            dto.setInstructorName(section.getInstructorId() != null && !section.getInstructorId().isBlank()
                    ? section.getInstructorId()
                    : "TBD");
        }

        if (course != null) {
            dto.setCourseId(course.getCourseId());
            dto.setCourseCode(course.getCourseCode());
            dto.setCourseName(course.getCourseName());
            dto.setCredits(3);
        }

        dto.setTerm(enrollment.getPlannedTerm());
        return dto;
    }

    private String buildSectionCode(Section section) {
        if (section.getCourse() != null && section.getCourse().getCourseCode() != null) {
            return section.getCourse().getCourseCode() + "-" + section.getSectionId();
        }
        return String.valueOf(section.getSectionId());
    }

    private String resolveTermName(Section section) {
        if (section.getTermId() == null) {
            return "";
        }

        String name = section.getTermId().getTermName();
        if (name == null || name.isBlank()) {
            return "";
        }

        if (name.toLowerCase().contains("spring")) {
            return "Term 1";
        }

        if (name.toLowerCase().contains("summer")) {
            return "Term 2";
        }

        return name;
    }

    private boolean schedulesConflict(String scheduleA, String scheduleB) {
        ParsedSchedule a = parseSchedule(scheduleA);
        ParsedSchedule b = parseSchedule(scheduleB);

        if (a == null || b == null) {
            return false;
        }

        boolean sharedDay = a.days.stream().anyMatch(b.days::contains);
        if (!sharedDay) {
            return false;
        }

        return a.start < b.end && b.start < a.end;
    }

    private ParsedSchedule parseSchedule(String schedule) {
        if (schedule == null || !schedule.contains(" ")) {
            return null;
        }

        int firstSpace = schedule.indexOf(" ");
        String daysPart = schedule.substring(0, firstSpace).trim();
        String timePart = schedule.substring(firstSpace + 1).trim();

        String[] timeRange = timePart.split(" - ");
        if (timeRange.length != 2) {
            return null;
        }

        ParsedSchedule parsed = new ParsedSchedule();
        parsed.days = List.of(daysPart.split("/"));
        parsed.start = convertToMinutes(timeRange[0].trim());
        parsed.end = convertToMinutes(timeRange[1].trim());

        return parsed;
    }

    private int convertToMinutes(String timeStr) {
        String[] parts = timeStr.split(" ");
        if (parts.length != 2) {
            return 0;
        }

        String time = parts[0];
        String meridiem = parts[1];

        String[] hm = time.split(":");
        int hours = Integer.parseInt(hm[0]);
        int minutes = Integer.parseInt(hm[1]);

        if ("PM".equalsIgnoreCase(meridiem) && hours != 12) {
            hours += 12;
        }

        if ("AM".equalsIgnoreCase(meridiem) && hours == 12) {
            hours = 0;
        }

        return hours * 60 + minutes;
    }

    private static class ParsedSchedule {
        private List<String> days;
        private int start;
        private int end;
    }
}