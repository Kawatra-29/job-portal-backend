package com.saurabh.entity;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Candidate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long candidateid;
	
	private String Name; 
	private String contactNo;
	private String experience;
	private String email;
	private String education;
	private String skills;
	private String password;
	
	@OneToMany(mappedBy = "candidate") ///Foreign key 
	@JsonIgnore
	List<Application> applications = new ArrayList<>();
	
	
	public void addApplication(Application app)
	{
		this.applications.add(app);
		app.setCandidate(this); 
	}
	
	public Candidate() {
		
	}

	public Candidate(Long candidateid, String name, String contactNo, String experience, String email, String education,
			String skills, String password) {
		super();
		this.candidateid = candidateid;
		Name = name;
		this.contactNo = contactNo;
		this.experience = experience;
		this.email = email;
		this.education = education;
		this.skills = skills;
		this.password = password;
	}

	public Long getCandidateid() {
		return candidateid;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
