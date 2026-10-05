package com.jarirahmed.production;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/** Async work is dispatched to a named executor instead of blocking the caller. */
@Service
public class AsyncNotificationService {
    @Async("advancedTaskExecutor")
    public CompletableFuture<String> process(String recipient, String message) {
        String result = "Processed for " + recipient + ": " + message
                + " on " + Thread.currentThread().getName();
        return CompletableFuture.completedFuture(result);
    }
}
