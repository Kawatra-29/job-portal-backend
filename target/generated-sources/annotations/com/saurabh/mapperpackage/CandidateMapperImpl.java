package com.saurabh.mapperpackage;

import com.saurabh.DTOs.RegisterCandidateDTO;
import com.saurabh.entity.Candidate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T20:22:28+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 25.0.1 (Eclipse Adoptium)"
)
@Component
public class CandidateMapperImpl implements CandidateMapper {

    @Override
    public Candidate toEntity(RegisterCandidateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Candidate candidate = new Candidate();

        candidate.setName( dto.getName() );
        candidate.setContactNo( dto.getContactNo() );
        candidate.setExperience( dto.getExperience() );
        candidate.setEmail( dto.getEmail() );
        candidate.setEducation( dto.getEducation() );
        candidate.setSkills( dto.getSkills() );
        candidate.setPassword( dto.getPassword() );

        return candidate;
    }

    @Override
    public RegisterCandidateDTO toDTO(Candidate entity) {
        if ( entity == null ) {
            return null;
        }

        RegisterCandidateDTO registerCandidateDTO = new RegisterCandidateDTO();

        registerCandidateDTO.setName( entity.getName() );
        registerCandidateDTO.setContactNo( entity.getContactNo() );
        registerCandidateDTO.setExperience( entity.getExperience() );
        registerCandidateDTO.setEmail( entity.getEmail() );
        registerCandidateDTO.setPassword( entity.getPassword() );
        registerCandidateDTO.setEducation( entity.getEducation() );
        registerCandidateDTO.setSkills( entity.getSkills() );

        return registerCandidateDTO;
    }
}
