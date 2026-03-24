package com.saurabh.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saurabh.Entity.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface JobRepository extends JpaRepository<Job, Long> {

    
	@NonNull
	Optional<Job> findById(@NonNull Long id);

    List<Job> findByEmployerId(Long employerId);

    // FIX 12: Uncommented and fixed the N+1 query solution.
    //
    // Before: when you loaded jobs and then accessed job.getApplications()
    // for each one, Hibernate fired a separate SELECT for every single job.
    // With 100 jobs that is 101 queries (1 for jobs + 100 for applications).
    //
    // This JOIN FETCH loads jobs AND their applications in one query.
    // Use this method in EmployerController instead of findByEmployerId
    // when you need to show application counts alongside job listings.
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.applications WHERE j.employer.id = :employerId")
    List<Job> findByEmployerIdWithApplications(@Param("employerId") Long employerId);
}
