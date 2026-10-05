package com.jarirahmed.springcore.service;

import java.util.UUID;

/** Each prototype lookup receives a new marker instance. */
public final class PrototypeMarker {
    private final UUID instanceId = UUID.randomUUID();

    public UUID instanceId() {
        return instanceId;
    }
}
