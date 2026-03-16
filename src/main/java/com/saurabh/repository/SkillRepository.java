package com.saurabh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{

	Skill findByName(String skillName);

}
