package com.saurabh.repository;
import org.springframework.data.repository.CrudRepository;

import com.saurabh.entity.Application;

public interface AppRepository extends CrudRepository<Application, Integer> {

}
