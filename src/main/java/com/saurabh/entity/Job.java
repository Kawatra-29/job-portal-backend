package com.saurabh.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.saurabh.ENUMS.ExperienceLevel;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.ENUMS.JobType;
import com.saurabh.ENUMS.WorkMode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jobs")
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employer_id")
	private Employer employer;

	@Column(columnDefinition = "TEXT")
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String requirements;

	@Column(columnDefinition = "TEXT")
	private String responsibilities;

	@Enumerated(EnumType.STRING)
	private JobType jobType;

	@Enumerated(EnumType.STRING)
	private WorkMode workMode;

	@Column(columnDefinition = "TEXT")
	private String location;

	private BigDecimal salaryMin;

	private BigDecimal salaryMax;

	private String currency;

	@Enumerated(EnumType.STRING)
	private ExperienceLevel experienceLevel;

	@Enumerated(EnumType.STRING)
	private JobStatus status;

	private LocalDate deadline;

	@OneToMany(mappedBy = "job")
	private List<Application> applications;

}
