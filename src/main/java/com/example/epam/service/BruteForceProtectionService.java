package com.example.epam.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final ConcurrentHashMap<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String username) {
        LoginAttempt attempt = attempts.computeIfAbsent(username, LoginAttempt::new);
        if (attempt.isBlocked()) {
            return;
        }
        attempt.incrementAttempts();
        if (attempt.getAttempts() == MAX_ATTEMPTS) {
            attempt.setLockoutStartTime(System.currentTimeMillis());
        }
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) {
            return false;
        }

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            long lockoutStartTime = attempt.getLockoutStartTime();
            if (System.currentTimeMillis() - lockoutStartTime < BLOCK_TIME_MILLIS) {
                return true;
            } else {
                attempts.remove(username);
            }
        }
        return false;
    }

    public void resetAttempts(String username) {
        attempts.remove(username);
    }

    private static class LoginAttempt {
        private int attempts;
        private long lastAttemptTime;
        private long lockoutStartTime;

        public LoginAttempt(String username) {
            this.attempts = 0;
            this.lastAttemptTime = System.currentTimeMillis();
            this.lockoutStartTime = 0;
        }

        public void incrementAttempts() {
            this.attempts++;
            this.lastAttemptTime = System.currentTimeMillis();
        }

        public int getAttempts() {
            return attempts;
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }

        public long getLockoutStartTime() {
            return lockoutStartTime;
        }

        public void setLockoutStartTime(long lockoutStartTime) {
            this.lockoutStartTime = lockoutStartTime;
        }

        public boolean isBlocked() {
            return lockoutStartTime > 0;
        }
    }
}