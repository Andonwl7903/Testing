package com.bridgetrack.bridgetrack.controller;

import com.bridgetrack.bridgetrack.dto.CreateAttendanceRecordRequest;
import com.bridgetrack.bridgetrack.dto.CreateAttendanceSessionRequest;
import com.bridgetrack.bridgetrack.model.AttendanceRecords;
import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.AttendanceRecordsRepository;
import com.bridgetrack.bridgetrack.repository.AttendanceSessionsRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceSessionsRepository attendanceSessionsRepository;
    private final AttendanceRecordsRepository attendanceRecordsRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public AttendanceController(
            AttendanceSessionsRepository attendanceSessionsRepository,
            AttendanceRecordsRepository attendanceRecordsRepository,
            SectionRepository sectionRepository,
            StudentRepository studentRepository
    ) {
        this.attendanceSessionsRepository = attendanceSessionsRepository;
        this.attendanceRecordsRepository = attendanceRecordsRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(@RequestBody CreateAttendanceSessionRequest request) {
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        AttendanceSessions session = new AttendanceSessions();
        session.setSection(section);
        session.setSessionDate(LocalDate.parse(request.getSessionDate()));
        session.setCreatedBy(request.getCreatedBy());

        AttendanceSessions saved = attendanceSessionsRepository.save(session);

        return ResponseEntity.ok(Map.of("id", saved.getAttendanceSessionId()));
    }

    @PostMapping("/records")
    public ResponseEntity<?> createRecord(@RequestBody CreateAttendanceRecordRequest request) {
        AttendanceSessions session = attendanceSessionsRepository.findById(request.getAttendanceSessionId())
                .orElseThrow(() -> new RuntimeException("Attendance session not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        AttendanceRecords record = new AttendanceRecords();
        record.setAttendanceSession(session);
        record.setStudent(student);
        record.setPresent(request.isPresent());

        attendanceRecordsRepository.save(record);

        return ResponseEntity.ok(Map.of("message", "Attendance record saved"));
    }
}