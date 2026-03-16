package com.saurabh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.JobSeekerSkill;
import com.saurabh.Entity.Skill;

public interface JobSeekerSkillRepository extends JpaRepository<JobSeekerSkill, Long> {

	boolean existsByJobSeekerAndSkill(JobSeeker jobSeeker, Skill skill);

	Optional<JobSeekerSkill> findByJobSeekerAndSkill(JobSeeker jobSeeker, Skill skill);

}
