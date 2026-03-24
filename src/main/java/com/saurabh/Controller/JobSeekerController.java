package com.saurabh.Controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saurabh.DTOs.JobSeekerDto;
import com.saurabh.DTOs.JobSeekerResponseDTO;
import com.saurabh.DTOs.SkillResponseDto;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.service.JobSeekerService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/jobseekers")
public class JobSeekerController {

	@Autowired
	private JobSeekerService jobSeekerService;

	@GetMapping("/me")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<JobSeekerResponseDTO> getUser(@AuthenticationPrincipal UserDetails userDetails) {

		return jobSeekerService.getProfile(userDetails);
	}

	@PutMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('JOBSEEKER')")
	public ResponseEntity<JobSeeker> updateUser(@RequestBody JobSeekerDto jobSeekerDto,
			@AuthenticationPrincipal UserDetails userDetails) {

		JobSeeker updatedJobSeeker = jobSeekerService.update(jobSeekerDto, userDetails);

		return ResponseEntity.ok(updatedJobSeeker);
	}

	@GetMapping("/me/skills")
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('JOBSEEKER')")
	public Set<SkillResponseDto> getSkills(@AuthenticationPrincipal UserDetails userDetails) {
		return jobSeekerService.getSkills(userDetails);
	}

	@PostMapping("/me/skills")
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('JOBSEEKER')")
	public ResponseEntity<?> addSkill(@RequestParam String request, @AuthenticationPrincipal UserDetails userDetails) {

		jobSeekerService.addSkill(request, userDetails);

		return ResponseEntity.status(HttpStatus.CREATED).body("Skill added successfully");
	}
	
	@DeleteMapping("/me/skills/{skillId}")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> deleteSkill(@PathVariable long skillId,@AuthenticationPrincipal UserDetails userDetails)
	{
		jobSeekerService.removeSkill(skillId, userDetails);
		
		return ResponseEntity.noContent().build();
	}
	
	
	
	

}

//| `GET` | `/jobseekers/me` | Get own seeker profile | Jobseeker |
//| `PUT` | `/jobseekers/me` | Update headline, summary, location, salary, etc. | Jobseeker |
//| `POST` | `/jobseekers/me/resume` | Upload resume PDF (multipart/form-data) | Jobseeker |
//| `GET` | `/jobseekers/me/skills` | List own skills with proficiency | Jobseeker |
//| `POST` | `/jobseekers/me/skills` | Add a skill to profile | Jobseeker |
//| `DELETE` | `/jobseekers/me/skills/{skillId}` | Remove a skill from profile | Jobseeker |
//| `GET` | `/jobseekers/{id}` | View a seeker's public profile | Employer |