package com.example.epam.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    private static final String HEALTH_CHECK_QUERY = "SELECT 1";
    private static final int TIMEOUT_SECONDS = 2;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        long startTime = System.nanoTime();
        try {
            jdbcTemplate.setQueryTimeout(TIMEOUT_SECONDS);

            jdbcTemplate.execute(HEALTH_CHECK_QUERY);

            // Calculate latency
            long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logger.info("Database health check succeeded with latency: {} ms", latencyMs);

            return Health.up()
                    .withDetail("database", "Service is running")
                    .withDetail("latencyMs", latencyMs)
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
        } catch (Exception e) {
            long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            String errorMessage = extractRootCauseMessage(e);
            logger.error("Database health check failed after {} ms: {}", latencyMs, errorMessage);

            return Health.down()
                    .withDetail("database", "Service is down")
                    .withDetail("error", errorMessage)
                    .withDetail("latencyMs", latencyMs)
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
        }
    }

    private String extractRootCauseMessage(Exception e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage() != null ? rootCause.getMessage() : e.getMessage();
    }
}