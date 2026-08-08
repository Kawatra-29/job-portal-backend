package com.saurabh.service;

import static org.junit.jupiter.api.Assertions.*;
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

import com.saurabh.DTOs.JobResponseDto;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.exception.ResourceNotFoundException;
import com.saurabh.mapper.JobMapper;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    // -------------------------------------------------------------
    // TEST 1: Positive Case - Retrieve job by ID successfully
    // -------------------------------------------------------------
    @Test
    @DisplayName("getJob() - Should return JobResponseDto when job ID exists")
    void testGetJob_Success() {
        // [A] Arrange: Set up mock data and Mockito expectations
        Job fakeJob = Job.builder().id(101L).title("Backend SDE").build();
        JobResponseDto fakeDto = mock(JobResponseDto.class);

        when(jobRepository.findById(101L)).thenReturn(Optional.of(fakeJob));
        when(jobMapper.toDTO(fakeJob)).thenReturn(fakeDto);

        // [A] Act: Invoke the service method
        JobResponseDto result = jobService.getJob(101L);

        // [A] Assert: Verify results and repository interactions
        assertNotNull(result);
        verify(jobRepository, times(1)).findById(101L);
    }

    // -------------------------------------------------------------
    // TEST 2: Negative Case - Job ID not found throws exception
    // -------------------------------------------------------------
    @Test
    @DisplayName("getJob() - Should throw ResourceNotFoundException when job ID does not exist")
    void testGetJob_NotFound_ThrowsException() {
        // [A] Arrange: Mock empty repository response
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        // [A] Act & Assert: Verify that ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJob(999L);
        });
    }

    // -------------------------------------------------------------
    // TEST 3: Security Case - Unauthorized deletion attempt
    // -------------------------------------------------------------
    @Test
    @DisplayName("deleteJob() - Should throw AccessDeniedException when another employer attempts deletion")
    void testDeleteJob_Unauthorized_ThrowsAccessDenied() {
        // [A] Arrange: Set up job owner and unauthorized caller
        Employer ownerEmployer = new Employer();
        ownerEmployer.setId(1L);

        Employer hackerEmployer = new Employer();
        hackerEmployer.setId(2L); // Different employer ID

        Job job = Job.builder().id(50L).employer(ownerEmployer).build();

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("unauthorized@gmail.com");

        when(employerRepository.findByUser_Email("unauthorized@gmail.com")).thenReturn(Optional.of(hackerEmployer));
        when(jobRepository.findById(50L)).thenReturn(Optional.of(job));

        // [A] Act & Assert: Verify AccessDeniedException is thrown
        assertThrows(AccessDeniedException.class, () -> {
            jobService.deleteJob(50L, mockUserDetails);
        });
    }
}
