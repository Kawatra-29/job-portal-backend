package com.saurabh.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private User user;

    private String companyName;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    private String description;

    @Builder.Default
    private Boolean isVerified = false;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Job> jobs;

}
