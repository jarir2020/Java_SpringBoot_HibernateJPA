package com.jarirahmed.javacore.advanced;

import com.jarirahmed.javacore.oop.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Small deterministic examples of shared state, executors, and futures.
 * Production code should also define cancellation and timeout policies.
 */
public final class ConcurrencyExamples {
    private ConcurrencyExamples() {
    }

    public static int runParallelCounter(int workerCount, int incrementsPerWorker)
            throws InterruptedException, ExecutionException {
        if (workerCount <= 0 || incrementsPerWorker < 0) {
            throw new IllegalArgumentException("Workers must be positive and increments cannot be negative.");
        }

        SafeCounter counter = new SafeCounter();
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<java.util.concurrent.Future<?>> tasks = new ArrayList<>();
            for (int worker = 0; worker < workerCount; worker++) {
                tasks.add(executor.submit(() -> {
                    for (int increment = 0; increment < incrementsPerWorker; increment++) {
                        counter.increment();
                    }
                }));
            }
            for (var task : tasks) {
                task.get();
            }
            return counter.value();
        } finally {
            executor.shutdown();
        }
    }

    public static CompletableFuture<String> loadEmployeeLabelAsync(Employee employee) {
        return CompletableFuture.supplyAsync(
                () -> "%d:%s".formatted(employee.getId(), employee.getName()));
    }

    /** synchronized makes the read/modify/write operation one critical section. */
    private static final class SafeCounter {
        private int value;

        private synchronized void increment() {
            value++;
        }

        private synchronized int value() {
            return value;
        }
    }
}
