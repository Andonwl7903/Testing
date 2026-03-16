package com.bridgetrack.bridgetrack.repository;

import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSessionsRepository extends JpaRepository<AttendanceSessions, Long> {
}