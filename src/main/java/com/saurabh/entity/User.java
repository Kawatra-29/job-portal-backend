package com.saurabh.entity;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.saurabh.ENUMS.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users", indexes = {
	    @Index(name = "idx_email", columnList = "email"),
	    @Index(name = "idx_location", columnList = "location")
	})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@SuppressWarnings("serial")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Candidate candidate;

	@Override
	public @Nullable String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

}