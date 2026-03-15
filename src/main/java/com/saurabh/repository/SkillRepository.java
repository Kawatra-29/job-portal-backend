package com.saurabh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{

	List<String> findByName(String skillName);

}
