package com.bridgetrack.bridgetrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bridgetrack.bridgetrack.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
	long count();
	
	@Query("SELECT COUNT(s.sectionId) FROM Section s WHERE s.sectionId IS NOT NULL")
    long countSectionIds();
}
