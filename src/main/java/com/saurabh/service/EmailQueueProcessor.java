package com.saurabh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurabh.DTOs.EmailTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * EmailQueueProcessor — Redis email queue ka consumer.
 *
 * EmailService.enqueueOrSend() har email task ko Redis List (email_queue)
 * mein rightPush karta hai. Yahan hum har 5 second pe leftPop karte hain
 * (FIFO order) aur EmailService.sendDirectEmail() se actual email bhejte hain.
 *
 * Flow:
 *   AuthService → EmailService.sendOtpEmail()
 *              → enqueueOrSend()
 *              → rightPush("email_queue", json)   ← produce
 *                                                  |
 *   EmailQueueProcessor (every 5s)                ↓
 *              ← leftPop("email_queue")           ← consume
 *              → sendDirectEmail(task)
 */
@Component
public class EmailQueueProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EmailQueueProcessor.class);

    // Ek baar mein kitni emails process karni hain
    private static final int BATCH_SIZE = 10;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Har 5 saniye mein Redis queue se emails uthata hai aur bhejtaa hai.
     * fixedDelay = previous run khatam hone ke baad 5s wait karta hai,
     * jisse concurrent processing nahi hoti.
     */
    @Scheduled(fixedDelay = 5000)
    public void processEmailQueue() {
        if (redisTemplate == null) {
            // Redis available nahi hai — EmailService direct send mode mein kaam karega
            return;
        }

        int processed = 0;

        try {
            // BATCH_SIZE tak emails ek baar mein process karo
            for (int i = 0; i < BATCH_SIZE; i++) {
                // leftPop — FIFO (pehle aayi, pehle jaayegi)
                String json = redisTemplate.opsForList().leftPop(EmailService.EMAIL_QUEUE_KEY);

                if (json == null) {
                    // Queue khali hai — is batch mein aur kuch nahi
                    break;
                }

                try {
                    EmailTaskDto task = objectMapper.readValue(json, EmailTaskDto.class);
                    emailService.sendDirectEmail(task);
                    processed++;
                } catch (Exception e) {
                    logger.error("Email task deserialize/send mein error: {}. Payload: {}", e.getMessage(), json);
                    // Bad message ko discard karte hain — dobara queue mein nahi daalta
                    // (infinite loop avoid karne ke liye)
                }
            }
        } catch (Exception e) {
            logger.error("Redis email queue se connect karne mein error: {}", e.getMessage());
        }

        if (processed > 0) {
            logger.info("Email queue: {} email(s) is batch mein successfully process ki.", processed);
        }
    }
}