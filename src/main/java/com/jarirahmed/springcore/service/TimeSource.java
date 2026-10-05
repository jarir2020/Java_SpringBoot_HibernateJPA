package com.jarirahmed.springcore.service;

import java.time.Instant;

/** An abstraction makes time replaceable in a deterministic unit test. */
public interface TimeSource {
    Instant now();
}
