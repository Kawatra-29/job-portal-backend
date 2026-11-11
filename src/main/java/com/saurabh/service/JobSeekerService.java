package com.saurabh.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

import com.saurabh.DTOs.JobSeekerDto;
import com.saurabh.DTOs.JobSeekerResponseDTO;
import com.saurabh.DTOs.SkillResponseDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.JobSeekerSkill;
import com.saurabh.Entity.Skill;
import com.saurabh.Entity.User;
import com.saurabh.exception.ResourceNotFoundException;
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

	@Value("${app.upload.dir:uploads/resumes}")
	private String uploadDir;

	private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024; // 5 MB

	@Transactional(readOnly = true)
	public ResponseEntity<JobSeekerResponseDTO> getProfile(UserDetails userDetails) {

		JobSeeker profile = jobseekerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		Set<SkillResponseDto> skills = Optional.ofNullable(profile.getSkills())
				.orElse(Collections.emptySet())
				.stream()
				.map(jsSkill -> new SkillResponseDto(
						jsSkill.getSkill().getName(),
						jsSkill.getProficiencyLevel()))
				.collect(Collectors.toSet());

		var user = profile.getUser();

		UserResponseDTO userDto = new UserResponseDTO(
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getPhone(),
				user.getRole().name());

		JobSeekerResponseDTO response = new JobSeekerResponseDTO(
				profile.getId(),
				profile.getHeadline(),
				profile.getSummary(),
				profile.getLocation(),
				profile.getYearsOfExperience(),
				profile.getExpectedSalary(),
				profile.getAvailability() != null ? profile.getAvailability().name() : null,
				profile.getResumeUrl(),
				skills,
				userDto);

		return ResponseEntity.ok(response);
	}

	@Transactional
	public JobSeekerResponseDTO update(JobSeekerDto jobSeekerDto, UserDetails userDetails) {

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		JobSeeker jobSeekerProfile2 = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

		jobSeekerProfile2.setAvailability(jobSeekerDto.availability());
		jobSeekerProfile2.setExpectedSalary(jobSeekerDto.expectedSalary());
		jobSeekerProfile2.setHeadline(jobSeekerDto.headline());
		jobSeekerProfile2.setLocation(jobSeekerDto.location());
		jobSeekerProfile2.setSummary(jobSeekerDto.summary());
		jobSeekerProfile2.setYearsOfExperience(jobSeekerDto.yearsOfExperience());

		if (jobSeekerDto.skills() != null) {
			Set<JobSeekerSkill> skills = jobSeekerDto.skills().stream().map(skillDto -> {

				@SuppressWarnings("null")
				Skill skill = skillRepository.findById(skillDto.skillId())
						.orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

				JobSeekerSkill jsSkill = new JobSeekerSkill();
				jsSkill.setJobSeeker(jobSeekerProfile2);
				jsSkill.setSkill(skill);
				jsSkill.setProficiencyLevel(skillDto.level());

				return jsSkill;

			}).collect(Collectors.toSet());

			if (jobSeekerProfile2.getSkills() != null) {
				jobSeekerProfile2.getSkills().clear();
				jobSeekerProfile2.getSkills().addAll(skills);
			} else {
				jobSeekerProfile2.setSkills(skills);
			}
		}

		jobseekerRepository.save(jobSeekerProfile2);

		// Issue 9 Fix: Entity ki jagah DTO return karo — entity expose nahi honi chahiye.
		// getProfile() wala same DTO mapping reuse kar rahe hain.
		Set<SkillResponseDto> skillDtos = Optional.ofNullable(jobSeekerProfile2.getSkills())
				.orElse(Collections.emptySet())
				.stream()
				.map(jsSkill -> new SkillResponseDto(jsSkill.getSkill().getName(), jsSkill.getProficiencyLevel()))
				.collect(Collectors.toSet());

		UserResponseDTO userDto = new UserResponseDTO(
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getPhone(),
				user.getRole().name());

		return new JobSeekerResponseDTO(
				jobSeekerProfile2.getId(),
				jobSeekerProfile2.getHeadline(),
				jobSeekerProfile2.getSummary(),
				jobSeekerProfile2.getLocation(),
				jobSeekerProfile2.getYearsOfExperience(),
				jobSeekerProfile2.getExpectedSalary(),
				jobSeekerProfile2.getAvailability() != null ? jobSeekerProfile2.getAvailability().name() : null,
				jobSeekerProfile2.getResumeUrl(),
				skillDtos,
				userDto);
	}

	@Transactional(readOnly = true)
	public Set<SkillResponseDto> getSkills(UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		JobSeeker jobSeekerProfile = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

		return jobSeekerProfile.getSkills().stream()
				.map(jsSkill -> new SkillResponseDto(jsSkill.getSkill().getName(), jsSkill.getProficiencyLevel()))
				.collect(Collectors.toSet());

	}

	@SuppressWarnings("null")
	@Transactional
	public void addSkill(String skillName, UserDetails userDetails) {

		JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		Skill skill = Optional.ofNullable(skillRepository.findByName(skillName))
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillName));

		if (jobSeekerSkillRepository.existsByJobSeekerAndSkill(jobSeeker, skill)) {
			throw new IllegalStateException("Skill already added: " + skillName);
		}

		JobSeekerSkill jobSeekerSkill = JobSeekerSkill.builder()
				.jobSeeker(jobSeeker)
				.skill(skill)
				.build();

		jobSeekerSkillRepository.save(jobSeekerSkill);
	}

	@SuppressWarnings("null")
	@Transactional
	public void removeSkill(Long skillId, UserDetails userDetails) {

		String email = userDetails.getUsername();

		JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(email)
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		Skill skill = skillRepository.findById(skillId)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

		JobSeekerSkill jobSeekerSkill = jobSeekerSkillRepository
				.findByJobSeekerAndSkill(jobSeeker, skill)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not assigned"));

		jobSeekerSkillRepository.delete(jobSeekerSkill);
	}

	@Transactional
	public String uploadResume(MultipartFile file, UserDetails userDetails) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}

		if (file.getSize() > MAX_RESUME_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum allowed limit (5MB)");
		}

		String originalFilename = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		}

		if (!fileExtension.equals(".pdf") && !fileExtension.equals(".doc") && !fileExtension.equals(".docx")) {
			throw new IllegalArgumentException("Only PDF, DOC, and DOCX files are allowed");
		}

		// Sanitize filename to prevent directory traversal
		String cleanedName = originalFilename != null ? StringUtils.cleanPath(originalFilename) : "resume" + fileExtension;
		if (cleanedName.contains("..") || cleanedName.contains("/") || cleanedName.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename containing path traversal characters");
		}

		try {
			Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
			Path targetLocation = uploadPath.resolve(uniqueFileName).normalize();

			// Security check: ensure target path is within upload directory
			if (!targetLocation.startsWith(uploadPath)) {
				throw new SecurityException("Path traversal attempt detected");
			}

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			String resumeUrl = "/uploads/resumes/" + uniqueFileName;

			JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

			jobSeeker.setResumeUrl(resumeUrl);
			jobseekerRepository.save(jobSeeker);

			return resumeUrl;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store resume file: " + e.getMessage(), e);
		}
	}

}
