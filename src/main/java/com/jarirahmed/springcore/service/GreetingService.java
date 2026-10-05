package com.jarirahmed.springcore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * A Spring-managed service using constructor injection for its collaborators.
 *
 * <p>The prefix is configuration data, not a service dependency, so Spring
 * supplies it through {@code @Value} at construction time.</p>
 */
@Service
public class GreetingService {
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    private final GreetingFormatter formatter;
    private final TimeSource timeSource;
    private final String prefix;

    public GreetingService(
            GreetingFormatter formatter,
            TimeSource timeSource,
            @Value("${app.greeting-prefix:Hello}") String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("Greeting prefix cannot be blank.");
        }
        this.formatter = formatter;
        this.timeSource = timeSource;
        this.prefix = prefix.trim();
    }

    public String createGreeting(String rawName) {
        return "%s, %s! UTC: %s".formatted(
                prefix,
                formatter.normalize(rawName),
                UTC_FORMATTER.format(timeSource.now()));
    }
}
