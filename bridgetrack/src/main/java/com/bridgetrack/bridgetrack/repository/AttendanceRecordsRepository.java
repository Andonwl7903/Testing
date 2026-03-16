package com.bridgetrack.bridgetrack.repository;

import com.bridgetrack.bridgetrack.model.AttendanceRecords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordsRepository extends JpaRepository<AttendanceRecords, Long> {
}