package com.saurabh.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "saved_jobs")
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private JobSeeker jobSeeker;

    @ManyToOne
    private Job job;

}