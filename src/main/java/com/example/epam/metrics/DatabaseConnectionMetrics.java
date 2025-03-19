package com.example.epam.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DatabaseConnectionMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeConnections;
    private final Counter connectionErrors;
    private final Timer connectionDuration;

    public DatabaseConnectionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.activeConnections = new AtomicInteger(0);
        this.connectionErrors = Counter.builder("db_connection_errors")
                .description("Number of failed database connection attempts")
                .tag("type", "database")
                .register(meterRegistry);
        this.connectionDuration = Timer.builder("db_connection_duration")
                .description("Time taken to establish database connections")
                .tag("type", "database")
                .register(meterRegistry);
    }

    @PostConstruct
    public void init() {
        meterRegistry.gauge("db_active_connections",
                Tags.of("type", "database"),
                activeConnections);
    }

    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }

    public void recordConnectionError() {
        connectionErrors.increment();
    }

    public void recordConnectionDuration(long durationMs) {
        connectionDuration.record(durationMs, TimeUnit.MILLISECONDS);
    }
}