package com.jarirahmed.production;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.time.Duration;
import java.util.Map;

/** Runs the local production-pattern lab without requiring external services. */
public final class ProductionAdvancedLesson {
    private ProductionAdvancedLesson() {
    }

    public static void run() throws Exception {
        System.out.println("\n=== PHASE 12: PRODUCTION / ADVANCED SPRING ===");
        System.out.println("Cache → async executor → scheduled job → deployable application");

        try (AnnotationConfigApplicationContext context = openContext("phase12-lesson", 100)) {
            AdvancedEmployeeCache cache = context.getBean(AdvancedEmployeeCache.class);
            AdvancedEmployee first = cache.findById(1);
            AdvancedEmployee second = cache.findById(1);
            System.out.println("Cache returns same value: " + first.equals(second));
            System.out.println("Backing-store loads after two reads: "
                    + cache.backingStoreLoadCount());

            cache.evict(1);
            cache.findById(1);
            System.out.println("Backing-store loads after eviction and read: "
                    + cache.backingStoreLoadCount());

            String notification = context.getBean(AsyncNotificationService.class)
                    .process("ops@example.com", "refresh complete")
                    .get();
            System.out.println("Async result: " + notification);

            waitForScheduledRun(context.getBean(BackgroundSyncJob.class), Duration.ofSeconds(1));
            System.out.println("Scheduled job runs observed: "
                    + context.getBean(BackgroundSyncJob.class).runCount());
        }

        System.out.println("Redis/RabbitMQ/Kafka adapters remain infrastructure choices");
        System.out.println("PHASE 12 COMPLETE");
    }

    public static AnnotationConfigApplicationContext openContext(
            String contextName,
            long scheduleDelayMilliseconds) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                contextName + "-properties",
                Map.of("production.schedule.delay-ms", scheduleDelayMilliseconds)));
        context.register(ProductionAdvancedConfiguration.class);
        context.refresh();
        return context;
    }

    private static void waitForScheduledRun(
            BackgroundSyncJob job,
            Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (job.runCount() == 0 && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
    }
}
