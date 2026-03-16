package com.saurabh.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.saurabh.ENUMS.ProficiencyLevel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jobseeker_id")
    @JsonBackReference
    private JobSeeker jobSeeker;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel;

}