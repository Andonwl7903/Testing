package com.bridgetrack.bridgetrack.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;
    
    @Column(name = "instructor")
    private String instructorId;
    
    @Column(name = "meeting_days_times")
    private String meetingDaysTimes;
    
    @Column(name = "modality")
    private String modality;
    
    @Column(name = "course_requires_prereq")
    private Boolean courseRequiresPrereq;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    //May need to get rid of this data is not in the database
    private String course_category;

  
    public Section() {}

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Term getTermId() { return term; }
    public void setTermId(Term term) { this.term = term; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public String getMeeting_days_times() { return meetingDaysTimes; }
    public void setMeeting_days_times(String meetingDaysTimes) { this.meetingDaysTimes = meetingDaysTimes; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Boolean getCourse_requires_prereq() { return courseRequiresPrereq; }
    public void setCourse_requires_prereq(Boolean courseRequiresPrereq) { this.courseRequiresPrereq = courseRequiresPrereq; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getCourse_category() { return course_category; }
    public void setCourse_category(String course_category) { this.course_category = course_category;}
}


