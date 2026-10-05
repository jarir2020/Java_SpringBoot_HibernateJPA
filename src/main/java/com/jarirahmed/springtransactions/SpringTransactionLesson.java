package com.jarirahmed.springtransactions;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.List;
import java.util.Map;

/** Runs the Phase 8 transaction proxy lesson without Spring Boot shortcuts. */
public final class SpringTransactionLesson {
    private SpringTransactionLesson() {
    }

    public static void run() {
        System.out.println("\n=== PHASE 8: SPRING TRANSACTIONS ===");
        System.out.println("Proxy boundary: service method → @Transactional → EntityManager transaction");

        try (AnnotationConfigApplicationContext context = openContext("phase8-lesson")) {
            SpringTransactionService service = context.getBean(SpringTransactionService.class);
            service.seed();

            System.out.println("Transactional service is proxied: " + AopUtils.isAopProxy(service));
            System.out.println("Transaction active inside read method: " + service.transactionIsActive());
            System.out.println("Committed event id: " + service.createEvent("committed"));

            try {
                service.createEventThenThrowRuntime("runtime-rolled-back");
            } catch (IllegalStateException expected) {
                System.out.println("Runtime failure caused rollback: true");
            }

            try {
                service.createEventThenThrowChecked("checked-rolled-back");
            } catch (CheckedTransactionFailure expected) {
                System.out.println("Checked failure rollbackFor applied: true");
            }

            service.outerWithRequiresNewFailure();
            List<String> labels = service.eventLabels();
            System.out.println("Events after rollback tests: " + labels);
            System.out.println("REQUIRES_NEW inner event absent, outer events committed: "
                    + (labels.contains("outer-before")
                    && labels.contains("outer-after")
                    && !labels.contains("inner-rolled-back")));
            System.out.println("Lazy employees loaded inside transaction: "
                    + service.engineeringEmployeeCount());
        }

        System.out.println("PHASE 8 COMPLETE");
    }

    /** Test-friendly context factory with an isolated in-memory database name. */
    public static AnnotationConfigApplicationContext openContext(String databaseName) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                "phase8-properties",
                Map.of("phase8.database-name", databaseName)));
        context.register(SpringTransactionConfiguration.class);
        context.refresh();
        return context;
    }
}
