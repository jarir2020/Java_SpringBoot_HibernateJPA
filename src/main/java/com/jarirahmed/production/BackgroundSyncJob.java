package com.jarirahmed.production;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/** A small scheduled job; real jobs should be idempotent and observable. */
@Component
public class BackgroundSyncJob {
    private final AtomicInteger runCount = new AtomicInteger();

    @Scheduled(fixedDelayString = "${production.schedule.delay-ms:1000}")
    public void synchronize() {
        runCount.incrementAndGet();
    }

    public int runCount() {
        return runCount.get();
    }
}
