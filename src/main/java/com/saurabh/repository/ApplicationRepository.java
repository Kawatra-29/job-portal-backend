package com.saurabh.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.Application;
import com.saurabh.Entity.Job;
import com.saurabh.Entity.JobSeeker;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);
    
    boolean existsByJobAndJobSeeker(Job job, JobSeeker jobSeeker);

	List<Application> findByJobSeeker(JobSeeker jobSeeker);
}