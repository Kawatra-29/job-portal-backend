package com.saurabh.service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.saurabh.DTOs.JobSeekerDto;
import com.saurabh.DTOs.JobSeekerResponseDTO;
import com.saurabh.DTOs.SkillResponseDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.JobSeekerSkill;
import com.saurabh.Entity.Skill;
import com.saurabh.Entity.User;
import com.saurabh.repository.JobSeekerSkillRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.SkillRepository;
import com.saurabh.repository.UserRepository;

@Service
public class JobSeekerService {
	@Autowired
	private JobseekerRepository jobseekerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private JobSeekerSkillRepository jobSeekerSkillRepository;

	public ResponseEntity<JobSeekerResponseDTO> getProfile(UserDetails userDetails) {

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		JobSeeker profile =  jobseekerRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Profile not found"));
		
		Set<SkillResponseDto> skills = Optional.ofNullable(profile.getSkills())
		        .orElse(Collections.emptySet())
		        .stream()
		        .map(jsSkill -> new SkillResponseDto(
		                jsSkill.getSkill().getName(),
		                jsSkill.getProficiencyLevel()
		        ))
		        .collect(Collectors.toSet());
		
		UserResponseDTO userDto = new UserResponseDTO(
		        user.getId(),
		        user.getFullName(),
		        user.getEmail(),
		        user.getPhone(),
		        user.getRole().name()
		);
		
		JobSeekerResponseDTO response = new JobSeekerResponseDTO(
		        profile.getId(),
		        profile.getHeadline(),
		        profile.getSummary(),
		        profile.getLocation(),
		        profile.getYearsOfExperience(),
		        profile.getExpectedSalary(),
		        profile.getAvailability() != null ? profile.getAvailability().name() : null,
		        skills,
		        userDto
		);
		
		return ResponseEntity.ok(response);	
		
	}

	public JobSeeker update(JobSeekerDto jobSeekerDto, UserDetails userDetails) {

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		JobSeeker jobSeekerProfile2 = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Profile not found"));

		jobSeekerProfile2.setAvailability(jobSeekerDto.availability());
		jobSeekerProfile2.setExpectedSalary(jobSeekerDto.expectedSalary());
		jobSeekerProfile2.setHeadline(jobSeekerDto.headline());
		jobSeekerProfile2.setLocation(jobSeekerDto.location());
		jobSeekerProfile2.setSummary(jobSeekerDto.summary());
		jobSeekerProfile2.setYearsOfExperience(jobSeekerDto.yearsOfExperience());
		
		Set<JobSeekerSkill> skills = jobSeekerDto.skills().stream().map(skillDto -> {

			Skill skill = skillRepository.findById(skillDto.skillId())
					.orElseThrow(() -> new RuntimeException("Skill not found"));

			JobSeekerSkill jsSkill = new JobSeekerSkill();
			jsSkill.setJobSeeker(jobSeekerProfile2);
			jsSkill.setSkill(skill);
			jsSkill.setProficiencyLevel(skillDto.level());

			return jsSkill;

		}).collect(Collectors.toSet());

		jobSeekerProfile2.setSkills(skills);
		
		jobseekerRepository.save(jobSeekerProfile2);
		return jobSeekerProfile2;
	}

	public Set<SkillResponseDto> getSkills(UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		JobSeeker jobSeekerProfile = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Profile not found"));

		return jobSeekerProfile.getSkills().stream()
				.map(jsSkill -> new SkillResponseDto(jsSkill.getSkill().getName(), jsSkill.getProficiencyLevel()))
				.collect(Collectors.toSet());

	}

	public void addSkill(String skillName, UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(email);

	    Skill skill = skillRepository.findByName(skillName);
	    boolean exists = jobSeekerSkillRepository
	            .existsByJobSeekerAndSkill(jobSeeker, skill);

	    if(exists){
	        throw new RuntimeException("Skill already added");
	    }

	    JobSeekerSkill jobSeekerSkill = JobSeekerSkill.builder()
	            .jobSeeker(jobSeeker)
	            .skill(skill)
	            .build();

	    jobSeekerSkillRepository.save(jobSeekerSkill);
	}

	public void removeSkill(Long skillId, UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(email);

	    Skill skill = skillRepository.findById(skillId)
	            .orElseThrow(() -> new RuntimeException("Skill not found"));

	    JobSeekerSkill jobSeekerSkill =
	            jobSeekerSkillRepository
	            .findByJobSeekerAndSkill(jobSeeker, skill)
	            .orElseThrow(() -> new RuntimeException("Skill not assigned"));

	    jobSeekerSkillRepository.delete(jobSeekerSkill);
	}

}
