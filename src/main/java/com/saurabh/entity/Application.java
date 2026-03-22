package com.saurabh.Entity;


import java.time.LocalDateTime;

import com.saurabh.ENUMS.ApplicationStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor   // ← add
@AllArgsConstructor
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "JobSeeker_id")
    private JobSeeker jobSeeker;
	
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "job_id")
	private Job job;
	
	private LocalDateTime appliedAt;
	
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status; ///(APPLIED, SHORTLISTED, REJECTED etc....)

}
