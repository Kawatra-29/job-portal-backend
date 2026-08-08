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
@Table(name = "otp_verifications", indexes = { @Index(name = "idx_otp_email", columnList = "email") })
public class OtpVerification {

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
