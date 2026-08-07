package com.saurabh.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.saurabh.Entity.Job;
import com.saurabh.repository.JobRepository;

@Component
public class JobViewSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobViewSyncScheduler.class);
    private static final String VIEW_KEY_PREFIX = "job:views:";

    private final StringRedisTemplate redisTemplate;
    private final JobRepository jobRepository;

    public JobViewSyncScheduler(StringRedisTemplate redisTemplate, JobRepository jobRepository) {
        this.redisTemplate = redisTemplate;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 60000) // Runs every 60 seconds
    public void syncJobViewsToDatabase() {
        try {
            if (redisTemplate == null) return;

            Set<String> keys = redisTemplate.keys(VIEW_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) return;

            logger.info("Syncing {} job view counters from Redis to Database...", keys.size());

            for (String key : keys) {
                try {
                    String jobIdStr = key.substring(VIEW_KEY_PREFIX.length());
                    Long jobId = Long.parseLong(jobIdStr);
                    String countStr = redisTemplate.opsForValue().get(key);

                    if (countStr != null) {
                        long increment = Long.parseLong(countStr);
                        if (increment > 0) {
                            Job job = jobRepository.findById(jobId).orElse(null);
                            if (job != null) {
                                long currentViews = job.getViewsCount() != null ? job.getViewsCount() : 0L;
                                job.setViewsCount(currentViews + increment);
                                jobRepository.save(job);
                                redisTemplate.delete(key);
                                logger.info("Updated Job ID {} with {} additional views (Total: {})", jobId, increment, job.getViewsCount());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error syncing view count for key {}: {}", key, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("Redis unavailable during job view sync: {}", e.getMessage());
        }
    }
}
