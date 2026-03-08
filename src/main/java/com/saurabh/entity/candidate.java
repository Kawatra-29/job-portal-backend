package com.saurabh.entity;


import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long candidateid;
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	private String contactNo;
	private String experience;
	private String education;
	private String skills;
	
	@OneToMany(mappedBy = "candidate") ///Foreign key 
	List<Application> applications ;
	
}
