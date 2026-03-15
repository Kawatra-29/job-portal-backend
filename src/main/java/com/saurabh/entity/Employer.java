package com.saurabh.Entity;

import java.util.List;

import com.saurabh.ENUMS.CompanySize;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;

//    private String companyLogoUrl;

//    private String website;
//
//    private String industry;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

//    private Integer foundedYear;
//
    private String description;
//
//    private String headquarters;

    private Boolean isVerified;
    
    @OneToMany(mappedBy = "employer")
    private List<Job> jobs;

}