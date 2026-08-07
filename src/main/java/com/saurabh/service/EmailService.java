package com.saurabh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurabh.DTOs.EmailTaskDto;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    public static final String EMAIL_QUEUE_KEY = "email_queue";

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username:no-reply@stride.com}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        logger.info("--> [OTP GENERATED] Email: {}, OTP: {}", toEmail, otp);
        String subject = "Stride Job Portal - Verify Your Email OTP";
        String body = "Welcome to Stride Job Portal!\n\nYour 6-digit OTP for email verification is: " 
                + otp + "\n\nThis OTP is valid for 5 minutes. Do not share it with anyone.";
        EmailTaskDto task = new EmailTaskDto(toEmail, subject, body, "OTP");
        enqueueOrSend(task);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String otp) {
        logger.info("--> [RESET OTP GENERATED] Email: {}, OTP: {}", toEmail, otp);
        String subject = "Stride Job Portal - Reset Your Password";
        String body = "Hello,\n\nWe received a request to reset your Stride account password.\n\nYour 6-digit Password Reset OTP is: " 
                + otp + "\n\nThis OTP is valid for 15 minutes. If you did not request a password reset, please ignore this email.";
        EmailTaskDto task = new EmailTaskDto(toEmail, subject, body, "RESET_OTP");
        enqueueOrSend(task);
    }

    private void enqueueOrSend(EmailTaskDto task) {
        if (redisTemplate != null) {
            try {
                String json = objectMapper.writeValueAsString(task);
                redisTemplate.opsForList().rightPush(EMAIL_QUEUE_KEY, json);
                logger.info("Enqueued email task for {} into Redis queue {}", task.toEmail(), EMAIL_QUEUE_KEY);
                return;
            } catch (Exception e) {
                logger.warn("Failed to push email to Redis queue, executing direct send fallback: {}", e.getMessage());
            }
        }
        sendDirectEmail(task);
    }

    public void sendDirectEmail(EmailTaskDto task) {
        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. Email task for {} payload: {}", task.toEmail(), task.body());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(task.toEmail());
            message.setSubject(task.subject());
            message.setText(task.body());

            mailSender.send(message);
            logger.info("Successfully sent email [{}] to {}", task.type(), task.toEmail());
        } catch (Exception e) {
            logger.error("Failed to send email [{}] to {}: {}", task.type(), task.toEmail(), e.getMessage());
            logger.info("FALLBACK EMAIL PAYLOAD for {}: {}", task.toEmail(), task.body());
        }
    }
}

