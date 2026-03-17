package com.bridgetrack.bridgetrack.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Controller
public class AdminController {

	@Autowired
	private SectionRepository sectionRepository; 
	
	@Autowired
	private StudentRepository studentRepository; 
	
	@GetMapping("/admindashboard")
	public String adminPage(Model model) {
		long sectionCount = sectionRepository.count(); 
		long studentCount = studentRepository.count(); 
		
		long activeCount = studentRepository.countByStatusIgnoreCase("active"); 
		long inactiveCount = studentRepository.countByStatusIgnoreCase("inactive"); 
		
		long classCount = sectionRepository.countSectionIds();
		
		List<Student> students = studentRepository.findAll();
		
		List<Student> duplicateStudents = students.stream()
		        .filter(Student::isPossible_Duplicate)  
		        .collect(Collectors.toList());
		
		model.addAttribute("sectionCount", sectionCount); 
		model.addAttribute("studentCount", studentCount);
		model.addAttribute("activeCount", activeCount); 
		model.addAttribute("inactiveCount", inactiveCount); 
		model.addAttribute("students", duplicateStudents);
		model.addAttribute("classCount", classCount);
		return "admindashboard";
	}
}
