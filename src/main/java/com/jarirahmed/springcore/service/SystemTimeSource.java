package com.jarirahmed.springcore.service;

import java.time.Instant;

/** Production implementation supplied as a @Bean by the configuration class. */
public final class SystemTimeSource implements TimeSource {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
