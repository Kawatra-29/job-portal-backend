package com.saurabh.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_reset_tokens", indexes = { @Index(name = "idx_pwd_otp", columnList = "otp"),
		@Index(name = "idx_pwd_email", columnList = "email") })
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String otp;

	@Column(nullable = false)
	private LocalDateTime expiryTime;
}
