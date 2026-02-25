package com.saurabh.entity;

import java.time.LocalDate;
import com.saurabh.entity.Application.ApplicationStatus;
import jakarta.persistence.Id;


public class ApplyDTO {
	
	@Id
	private Long id;
	private int job_id;
	private int candidate_id;
	private LocalDate date;
	private ApplicationStatus status;

	public ApplyDTO() {
		super();
	}
	
	public ApplyDTO(Long id, int job_id, int candidate_id, LocalDate date, ApplicationStatus status) {
		super();
		this.id = id;
		this.job_id = job_id;
		this.candidate_id = candidate_id;
		this.date = date;
		this.status = status;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getJob_id() {
		return job_id;
	}

	public void setJob_id(int job_id) {
		this.job_id = job_id;
	}

	public int getCandidate_id() {
		return candidate_id;
	}

	public void setCandidate_id(int candidate_id) {
		this.candidate_id = candidate_id;
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
