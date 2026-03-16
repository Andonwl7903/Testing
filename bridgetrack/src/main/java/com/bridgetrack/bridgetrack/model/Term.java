package com.bridgetrack.bridgetrack.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "terms")
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Column(name = "name")
    private String termName; 
    
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

    //This data is not in the database may need to get rid of.
    private Boolean active;
    private Integer termCode;

    public Term() {}

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public Integer getTermCode() { return termCode; }
    public void setTermCode(Integer termCode) { this.termCode = termCode; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    //May need to get rid of
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
