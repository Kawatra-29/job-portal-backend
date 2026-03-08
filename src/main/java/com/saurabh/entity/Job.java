package com.saurabh.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

public class Job {
    @Id
    @GeneratedValue()
    private long id;
    @Column(nullable = false)
	private String title; 
	private String company;
	private String location;
	private String salrange;
	private String type;
	private String skillRequired;
	private String description;
	private LocalDate postDate;
	private LocalDate applyLastDate;
	@Builder.Default
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Application> applications = new ArrayList<>();
	
	public void addApplication(Application app)
	{
		this.applications.add(app);
		app.setJob(this); /// double check for database entry
	}
	
}
