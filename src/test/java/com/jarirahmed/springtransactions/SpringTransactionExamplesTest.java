package com.jarirahmed.springtransactions;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies Spring transaction proxies and rollback policies around JPA. */
class SpringTransactionExamplesTest {
    @Test
    void transactional_service_is_proxied_and_commits_work() {
        try (AnnotationConfigApplicationContext context =
                     SpringTransactionLesson.openContext("phase8-commit-test")) {
            SpringTransactionService service = context.getBean(SpringTransactionService.class);
            service.seed();

            assertTrue(AopUtils.isAopProxy(service));
            assertTrue(service.transactionIsActive());
            assertEquals(1L, service.createEvent("committed"));
            assertEquals(List.of("committed"), service.eventLabels());
        }
    }

    @Test
    void runtime_and_checked_failures_roll_back_with_their_policies() {
        try (AnnotationConfigApplicationContext context =
                     SpringTransactionLesson.openContext("phase8-rollback-test")) {
            SpringTransactionService service = context.getBean(SpringTransactionService.class);
            service.seed();
            service.createEvent("kept");

            try {
                service.createEventThenThrowRuntime("runtime-removed");
            } catch (IllegalStateException expected) {
                // Default @Transactional rollback behavior.
            }
            try {
                service.createEventThenThrowChecked("checked-removed");
            } catch (CheckedTransactionFailure expected) {
                // Explicit rollbackFor behavior.
            }

            assertEquals(List.of("kept"), service.eventLabels());
            assertFalse(service.eventLabels().contains("runtime-removed"));
            assertFalse(service.eventLabels().contains("checked-removed"));
        }
    }

    @Test
    void requires_new_rolls_back_inner_work_but_keeps_outer_work() {
        try (AnnotationConfigApplicationContext context =
                     SpringTransactionLesson.openContext("phase8-propagation-test")) {
            SpringTransactionService service = context.getBean(SpringTransactionService.class);
            service.seed();

            service.outerWithRequiresNewFailure();

            assertEquals(
                    List.of("outer-before", "outer-after"),
                    service.eventLabels());
        }
    }

    @Test
    void lazy_relationships_are_accessible_inside_transactional_service_method() {
        try (AnnotationConfigApplicationContext context =
                     SpringTransactionLesson.openContext("phase8-lazy-test")) {
            SpringTransactionService service = context.getBean(SpringTransactionService.class);
            service.seed();

            assertEquals(2, service.engineeringEmployeeCount());
        }
    }
}
