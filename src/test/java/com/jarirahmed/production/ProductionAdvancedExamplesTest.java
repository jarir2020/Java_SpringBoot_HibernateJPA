package com.jarirahmed.production;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies cache, async, and scheduling proxies in the final course phase. */
class ProductionAdvancedExamplesTest {
    @Test
    void cache_proxy_avoids_reloading_until_an_entry_is_evicted() {
        try (AnnotationConfigApplicationContext context =
                     ProductionAdvancedLesson.openContext("phase12-cache-test", 1000)) {
            AdvancedEmployeeCache cache = context.getBean(AdvancedEmployeeCache.class);

            assertTrue(AopUtils.isAopProxy(cache));
            assertEquals(cache.findById(1), cache.findById(1));
            assertEquals(1, cache.backingStoreLoadCount());

            cache.evict(1);
            cache.findById(1);

            assertEquals(2, cache.backingStoreLoadCount());
        }
    }

    @Test
    void async_service_returns_work_from_the_named_executor() throws Exception {
        try (AnnotationConfigApplicationContext context =
                     ProductionAdvancedLesson.openContext("phase12-async-test", 1000)) {
            AsyncNotificationService service = context.getBean(AsyncNotificationService.class);

            assertTrue(AopUtils.isAopProxy(service));
            String result = service.process("test@example.com", "hello")
                    .get();

            assertTrue(result.contains("test@example.com"));
            assertTrue(result.contains("advanced-worker-"));
        }
    }

    @Test
    void scheduled_job_runs_without_blocking_context_refresh() throws Exception {
        try (AnnotationConfigApplicationContext context =
                     ProductionAdvancedLesson.openContext("phase12-schedule-test", 25)) {
            BackgroundSyncJob job = context.getBean(BackgroundSyncJob.class);
            long deadline = System.nanoTime() + Duration.ofSeconds(1).toNanos();

            while (job.runCount() == 0 && System.nanoTime() < deadline) {
                Thread.sleep(10);
            }

            assertNotEquals(0, job.runCount());
        }
    }
}
