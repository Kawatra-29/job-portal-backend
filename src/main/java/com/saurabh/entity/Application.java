package com.saurabh.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "job_id")
	private Job job;
	
	private LocalDate date;
	
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status; ///(APPLIED, SHORTLISTED, REJECTED etc....)
	
	public enum ApplicationStatus {
	    APPLIED,
	    SHORTLISTED,
	    REJECTED,
	    SELECTED
	}
	
	

	public Application() {
		super();
	}

	public Application(Long id, com.saurabh.entity.Candidate candidate, Job job, LocalDate date,
			ApplicationStatus status) {
		super();
		this.id = id;
		this.candidate = candidate;
		this.job = job;
		this.date = date;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}
	

}
