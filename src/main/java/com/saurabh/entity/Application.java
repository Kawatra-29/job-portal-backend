package com.saurabh.entity;

import java.time.LocalDate;

import jakarta.persistence.*;



@Entity
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private candidate candidate;

	@ManyToOne
	@JoinColumn(name = "job_id")
	private Job job;
	
	private LocalDate date;
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status; ///(APPLIED, SHORTLISTED, REJECTED etc)
	
	public enum ApplicationStatus {
	    APPLIED,
	    SHORTLISTED,
	    REJECTED,
	    SELECTED
	}
	
	
	

}
