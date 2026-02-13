package com.saurabh.entity;


import jakarta.persistence.*;

@Entity
public class candidate {
	@Id
	private int candidateid;
	private String Name; 
	private String contcatNo;
	private String experience;
	private String email;
	private String education;
	private String skills;
	public candidate(int candidateid, String name, String contcatNo, String experience, String email, String education,
			String skills) {
		super();
		this.candidateid = candidateid;
		Name = name;
		this.contcatNo = contcatNo;
		this.experience = experience;
		this.email = email;
		this.education = education;
		this.skills = skills;
	}
	public int getCandidateid() {
		return candidateid;
	}
	public void setCandidateid(int candidateid) {
		this.candidateid = candidateid;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getContcatNo() {
		return contcatNo;
	}
	public void setContcatNo(String contcatNo) {
		this.contcatNo = contcatNo;
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
	
	
	
	
	
}
