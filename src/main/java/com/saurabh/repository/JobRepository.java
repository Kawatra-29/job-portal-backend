package com.saurabh.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.saurabh.Entity.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    @NonNull
    Optional<Job> findById(@Nullable Long id);

    List<Job> findByEmployerId(Long employerId);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.applications WHERE j.employer.id = :employerId")

    List<Job> findByEmployerIdWithApplications(@Param("employerId") Long employerId);

}
