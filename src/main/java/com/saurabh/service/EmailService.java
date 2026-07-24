package com.saurabh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@stride.com}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        logger.info("--> [OTP GENERATED] Email: {}, OTP: {}", toEmail, otp);

        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. OTP for {} is: {}", toEmail, otp);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Stride Job Portal - Verify Your Email OTP");
            message.setText("Welcome to Stride Job Portal!\n\nYour 6-digit OTP for email verification is: " 
                + otp + "\n\nThis OTP is valid for 5 minutes. Do not share it with anyone.");

            mailSender.send(message);
            logger.info("Successfully sent OTP email to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            logger.info("FALLBACK OTP for {}: {}", toEmail, otp);
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String otp) {
        logger.info("--> [RESET OTP GENERATED] Email: {}, OTP: {}", toEmail, otp);

        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. Reset OTP for {} is: {}", toEmail, otp);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Stride Job Portal - Reset Your Password");
            message.setText("Hello,\n\nWe received a request to reset your Stride account password.\n\nYour 6-digit Password Reset OTP is: " 
                + otp + "\n\nThis OTP is valid for 15 minutes. If you did not request a password reset, please ignore this email.");

            mailSender.send(message);
            logger.info("Successfully sent Password Reset OTP email to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send Password Reset OTP email to {}: {}", toEmail, e.getMessage());
            logger.info("FALLBACK RESET OTP for {}: {}", toEmail, otp);
        }
    }
}
