package com.saurabh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurabh.DTOs.EmailTaskDto;

@Component
public class EmailQueueProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EmailQueueProcessor.class);

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmailQueueProcessor(StringRedisTemplate redisTemplate, EmailService emailService) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 5000) // Polls queue every 5 seconds
    public void processEmailQueue() {
        try {
            if (redisTemplate == null) return;

            String jsonPayload;
            int processedCount = 0;

            // Process up to 10 email tasks per batch to prevent starving thread
            while (processedCount < 10 && (jsonPayload = redisTemplate.opsForList().leftPop(EmailService.EMAIL_QUEUE_KEY)) != null) {
                try {
                    EmailTaskDto task = objectMapper.readValue(jsonPayload, EmailTaskDto.class);
                    logger.info("Processing async email task for [{}] from Redis queue...", task.toEmail());
                    emailService.sendDirectEmail(task);
                    processedCount++;
                } catch (Exception e) {
                    logger.error("Failed to process email payload from Redis queue: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            // Quietly handle Redis disconnection warnings
        }
    }
}
