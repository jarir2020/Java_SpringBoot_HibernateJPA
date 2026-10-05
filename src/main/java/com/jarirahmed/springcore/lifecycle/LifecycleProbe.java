package com.jarirahmed.springcore.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/** Shows initialization and destruction callbacks on a managed singleton. */
@Component
public class LifecycleProbe {
    private boolean initialized;
    private boolean destroyed;

    @PostConstruct
    public void initialize() {
        initialized = true;
    }

    @PreDestroy
    public void destroy() {
        destroyed = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
