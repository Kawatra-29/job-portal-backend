package com.saurabh.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.saurabh.entity.Job;

//spring business service

@Service
public class JobService {
	private List<Job> jobs = Arrays.asList(
			new Job("Backend Developer Intern", "Google", "Los Angeles", "10 to 12 LPA", "Full Time", "Java,Python",
					"Backend Developer", LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 10)),
			new Job("Frontend Developer Intern", "Amazon", "San Francisco", "9 to 11 LPA", "Full Time",
					"Java,Python,HTML,CSS,JAVASCript", "Front end Developer", LocalDate.of(2026, 1, 6),
					LocalDate.of(2026, 1, 20)),
			new Job("full Stack Developer Intern", "Microsoft", "Ghaziabad", "12 to 15 LPA", "Full Time",
					"Java,Python,dotnet", "Backend Developer", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 25)),
			new Job("JAVA Developer Intern", "Google", "Lucknow", "10 to 15 LPA", "Full Time", "Java,Python",
					"Backend Developer", LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 19)),
			new Job("Backend Developer Intern", "Google", "Bangalore", "10-12 LPA", "Part Time",
					"Java, Spring Boot, MySQL", "Work on REST APIs and backend services", LocalDate.of(2026, 1, 1),
					LocalDate.of(2026, 2, 1)),

			new Job("Frontend Developer", "Amazon", "Hyderabad", "9-14 LPA", "Full Time", "React, JavaScript, CSS",
					"Build responsive UI components", LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 10)),

			new Job("Full Stack Developer", "Microsoft", "Noida", "12-18 LPA", "Full Time", "Java, React, MongoDB",
					"Handle both frontend and backend tasks", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 15)),

			new Job("Data Analyst", "TCS", "Pune", "6-10 LPA", "Part Time", "SQL, Excel, Power BI",
					"Analyze business data and generate reports", LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 30)));

	public List<Job> getAllJobs() {
		return jobs;
	}

	public Job getJob(String type) {
		return jobs.stream().filter(j -> j.getType().equals(type)).findFirst().get();
	}
}
