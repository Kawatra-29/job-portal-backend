package com.saurabh.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employer employer;

    @ManyToOne
    private User reviewer;

    private Integer rating;

    private String title;

    private String body;

}