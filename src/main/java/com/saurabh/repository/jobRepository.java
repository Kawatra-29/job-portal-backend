package com.saurabh.repository;

//////import org.springframework.data.jpa.repository.JpaRepository;including crud + adwance methods like findall
import org.springframework.data.repository.CrudRepository; ///only basic crud methods 

import com.saurabh.entity.Job;

public interface jobRepository extends CrudRepository<Job, Integer>{

}
