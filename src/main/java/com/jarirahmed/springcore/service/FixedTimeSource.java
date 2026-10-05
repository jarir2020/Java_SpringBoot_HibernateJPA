package com.jarirahmed.springcore.service;

import java.time.Instant;

/** Test-friendly implementation that always returns the supplied instant. */
public final class FixedTimeSource implements TimeSource {
    private final Instant instant;

    public FixedTimeSource(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Instant now() {
        return instant;
    }
}
