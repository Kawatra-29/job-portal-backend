package com.saurabh.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import com.saurabh.ENUMS.ApplicationStatus;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;
import com.saurabh.mapper.JobMapper;
import com.saurabh.repository.ApplicationRepository;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

	@Mock
	private ApplicationRepository applicationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JobseekerRepository jobseekerRepository;

	@Mock
	private JobRepository jobRepository;

	@Mock
	private EmployerRepository employerRepository;

	@Mock
	private JobMapper jobMapper;

	@InjectMocks
	private ApplicationService applicationService;

	@SuppressWarnings("null")
	@Test
	@DisplayName("applyJob() - Candidate successfully applies to a job")
	void testApplyJob_Success() {
		// Arrange
		Long jobId = 1L;
		UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("candidate@gmail.com");

		Job job = Job.builder().id(jobId).title("Java Engineer").status(JobStatus.OPEN).build();
		User user = new User();
		user.setEmail("candidate@gmail.com");
		JobSeeker jobSeeker = new JobSeeker();
		jobSeeker.setId(10L);

		when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
		when(userRepository.findByEmail("candidate@gmail.com")).thenReturn(Optional.of(user));
		when(jobseekerRepository.findByUser(user)).thenReturn(Optional.of(jobSeeker));
		when(applicationRepository.existsByJobAndJobSeeker(job, jobSeeker)).thenReturn(false);

		// Act
		applicationService.applyJob(jobId, userDetails);

		// Assert
		verify(applicationRepository, times(1)).save(any(Application.class));
	}

	@SuppressWarnings("null")
	@Test
	@DisplayName("applyJob() - Throws IllegalStateException when already applied")
	void testApplyJob_AlreadyApplied_ThrowsException() {
		// Arrange
		Long jobId = 1L;
		UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("candidate@gmail.com");

		Job job = Job.builder().id(jobId).status(JobStatus.OPEN).build();
		User user = new User();
		JobSeeker jobSeeker = new JobSeeker();
		jobSeeker.setId(10L);

		when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
		when(userRepository.findByEmail("candidate@gmail.com")).thenReturn(Optional.of(user));
		when(jobseekerRepository.findByUser(user)).thenReturn(Optional.of(jobSeeker));
		when(applicationRepository.existsByJobAndJobSeeker(job, jobSeeker)).thenReturn(true);

		// Act & Assert
		assertThrows(IllegalStateException.class, () -> {
			applicationService.applyJob(jobId, userDetails);
		});

		verify(applicationRepository, never()).save(any(Application.class));
	}

	@SuppressWarnings("null")
	@Test
	@DisplayName("updateStatus() - Unauthorized employer throws AccessDeniedException")
	void testUpdateStatus_UnauthorizedEmployer_ThrowsAccessDenied() {
		// Arrange
		Long appId = 50L;
		UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("unauthorized@gmail.com");

		Employer jobOwner = new Employer();
		jobOwner.setId(1L);

		Employer unauthorizedEmployer = new Employer();
		unauthorizedEmployer.setId(2L);

		Job job = Job.builder().id(100L).employer(jobOwner).build();
		Application app = Application.builder().id(appId).job(job).build();

		when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
		when(employerRepository.findByUser_Email("unauthorized@gmail.com"))
				.thenReturn(Optional.of(unauthorizedEmployer));

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			applicationService.updateStatus(appId, ApplicationStatus.ACCEPTED, userDetails);
		});

		verify(applicationRepository, never()).save(any(Application.class));
	}

	@SuppressWarnings("null")
	@Test
	@DisplayName("withdraw() - Candidate withdraws application successfully")
	void testWithdraw_Success() {
		// Arrange
		Long appId = 25L;
		UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("candidate@gmail.com");

		JobSeeker ownerSeeker = new JobSeeker();
		ownerSeeker.setId(5L);

		Application app = Application.builder().id(appId).jobSeeker(ownerSeeker).build();

		when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
		when(jobseekerRepository.findByUser_Email("candidate@gmail.com")).thenReturn(Optional.of(ownerSeeker));

		// Act
		applicationService.withdraw(appId, userDetails);

		// Assert
		verify(applicationRepository, times(1)).delete(app);
	}
}
