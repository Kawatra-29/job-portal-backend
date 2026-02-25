package com.saurabh.mapperpackage;

import org.mapstruct.Mapper;

import com.saurabh.DTOs.*;
import com.saurabh.entity.Candidate;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
	
    Candidate toEntity(RegisterCandidateDTO dto);

    RegisterCandidateDTO toDTO(Candidate entity);
}