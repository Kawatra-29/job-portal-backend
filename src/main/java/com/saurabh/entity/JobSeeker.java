package com.saurabh.Entity;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.saurabh.ENUMS.Availability;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobseeker_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeeker {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String headline;

    private String summary;

    private String location;

    private Integer yearsOfExperience;

    private BigDecimal expectedSalary;
    
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<JobSeekerSkill> skills;

    @Enumerated(EnumType.STRING)
    private Availability availability;
//    private String resumeUrl;
//    private String linkedinUrl;
//
//    private String githubUrl;

}