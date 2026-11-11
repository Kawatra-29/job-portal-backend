package com.saurabh.service;

import com.saurabh.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class JobViewSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobViewSyncScheduler.class);
    private static final String JOB_VIEWS_PREFIX = "job:views:";
    private static final String JOB_VIEWS_PATTERN = JOB_VIEWS_PREFIX + "*";

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JobRepository jobRepository;

    @Scheduled(fixedDelay = 120_000, initialDelay = 60_000)
    @Transactional
    public void syncJobViewsToDB() {
        if (redisTemplate == null) {
            return;
        }

        // Non-blocking SCAN instead of blocking KEYS *
        Set<String> keys = new HashSet<>();
        try {
            ScanOptions options = ScanOptions.scanOptions().match(JOB_VIEWS_PATTERN).count(100).build();
            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext() && keys.size() < 500) {
                    keys.add(cursor.next());
                }
            }
        } catch (Exception e) {
            logger.error("Error scanning job view keys from Redis: {}", e.getMessage());
            return;
        }

        if (keys.isEmpty()) {
            return;
        }

        int syncedCount = 0;

        for (String key : keys) {
            try {
                String idStr = key.substring(JOB_VIEWS_PREFIX.length());
                Long jobId = Long.parseLong(idStr);

                // Atomic get and delete avoids losing view counts arrived during sync
                String rawValue = redisTemplate.opsForValue().getAndDelete(key);
                if (rawValue == null) continue;

                long viewsToAdd = Long.parseLong(rawValue);
                if (viewsToAdd <= 0) {
                    continue;
                }

                jobRepository.findById(jobId).ifPresentOrElse(job -> {
                    long currentViews = job.getViewsCount() != null ? job.getViewsCount() : 0L;
                    job.setViewsCount(currentViews + viewsToAdd);
                    jobRepository.save(job);
                    logger.debug("Synced {} views to DB for job ID {}", viewsToAdd, jobId);
                }, () -> {
                    logger.warn("Job ID {} found in Redis view cache but not in DB.", jobId);
                });

                syncedCount++;

            } catch (NumberFormatException e) {
                logger.warn("Invalid job view key format: {}. Deleting key.", key);
                redisTemplate.delete(key);
            } catch (Exception e) {
                logger.error("Error syncing job view for key {}: {}", key, e.getMessage());
            }
        }

        if (syncedCount > 0) {
            logger.info("Job view sync completed: updated views for {} job(s).", syncedCount);
        }
    }
}
