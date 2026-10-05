package com.jarirahmed.projects.hrm.cache;

import com.jarirahmed.projects.hrm.dto.PayrollReportResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A local Redis-shaped adapter for the lesson.
 *
 * <p>The key/value/TTL boundary is what the service needs from Redis. This
 * version uses a concurrent map so the normal tutorial runs without Docker.
 * Replacing it with Spring Data Redis later does not change the service API.</p>
 */
@Component
public class RedisStyleReportCache implements ReportCache {
    private final Map<String, Entry> values = new ConcurrentHashMap<>();
    private final Duration ttl = Duration.ofMinutes(10);

    @Override
    public Optional<PayrollReportResponse> get(String key) {
        Entry entry = values.get(key);
        if (entry == null || entry.expiresAt().isBefore(Instant.now())) {
            values.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.report());
    }

    @Override
    public void put(String key, PayrollReportResponse report) {
        values.put(key, new Entry(report, Instant.now().plus(ttl)));
    }

    @Override
    public void evict(String key) {
        values.remove(key);
    }

    private record Entry(PayrollReportResponse report, Instant expiresAt) {
    }
}
