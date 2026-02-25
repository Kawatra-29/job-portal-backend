package com.saurabh.DTOs;

import jakarta.validation.constraints.*;

public class RegisterCandidateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number")
    private String contactNo;

    @NotBlank(message = "Experience is required")
    private String experience;

    @Email(message = "Invalid email")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 15)
    private String password;

    private String education;

    private String skills;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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