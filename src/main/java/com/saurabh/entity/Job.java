package com.saurabh.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Job {
    @Id
    private int id;
	private String title; 
	private String company;
	private String location;
	private String salrange;
	private String type;
	private String skillRequired;
	private String description;
	private LocalDate postDate;
	private LocalDate applyLastDate;
	
	@OneToMany(mappedBy = "job") // already madee table mapping 
	private List<Application> applications =new ArrayList<>();	
	
	public void addApplication(Application app)
	{
		this.applications.add(app);
		app.setJob(this); /// double check for database entry
	}
	
	public Job() {
		
	}
	
	public Job(int id, String title, String company, String location, String salrange, String type,
			String skillRequired, String description, LocalDate postDate, LocalDate applyLastDate) {
		super();
		this.id=id;
		this.title = title;
		this.company = company;
		this.location = location;
		this.salrange = salrange;
		this.type = type;
		this.skillRequired = skillRequired;
		this.description = description;
		this.postDate = postDate;
		this.applyLastDate = applyLastDate;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSalrange() {
		return salrange;
	}

	public void setSalrange(String salrange) {
		this.salrange = salrange;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSkillRequired() {
		return skillRequired;
	}

	public void setSkillRequired(String skillRequired) {
		this.skillRequired = skillRequired;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDate postDate) {
		this.postDate = postDate;
	}

	public LocalDate getApplyLastDate() {
		return applyLastDate;
	}

	public void setApplyLastDate(LocalDate applyLastDate) {
		this.applyLastDate = applyLastDate;
	}

}
