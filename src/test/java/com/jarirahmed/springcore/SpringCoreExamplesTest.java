package com.jarirahmed.springcore;

import com.jarirahmed.springcore.aop.MethodLoggingAspect;
import com.jarirahmed.springcore.container.SpringCoreApplication;
import com.jarirahmed.springcore.lifecycle.LifecycleProbe;
import com.jarirahmed.springcore.service.FixedTimeSource;
import com.jarirahmed.springcore.service.GreetingFormatter;
import com.jarirahmed.springcore.service.GreetingService;
import com.jarirahmed.springcore.service.PrototypeMarker;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringCoreExamplesTest {
    @Test
    void constructor_injection_keeps_a_service_deterministic_without_a_container() {
        GreetingService service = new GreetingService(
                new GreetingFormatter(),
                new FixedTimeSource(Instant.parse("2026-10-05T06:30:00Z")),
                "Hello");

        assertEquals("Hello, Jarir! UTC: 2026-10-05 06:30:00",
                service.createGreeting(" Jarir "));
    }

    @Test
    void application_context_scans_components_and_injects_configuration() {
        try (AnnotationConfigApplicationContext context = SpringCoreApplication.createContext(
                Map.of("app.greeting-prefix", "Welcome"))) {
            GreetingService service = context.getBean(GreetingService.class);

            assertTrue(service.createGreeting(" Jarir ").startsWith("Welcome, Jarir! UTC: "));
            assertTrue(AopUtils.isAopProxy(service));
        }
    }

    @Test
    void bean_scopes_follow_the_container_rules() {
        try (AnnotationConfigApplicationContext context = SpringCoreApplication.createContext(Map.of())) {
            assertSame(context.getBean(GreetingService.class), context.getBean(GreetingService.class));
            assertNotSame(context.getBean(PrototypeMarker.class), context.getBean(PrototypeMarker.class));
        }
    }

    @Test
    void lifecycle_callbacks_run_on_startup_and_context_close() {
        LifecycleProbe probe;
        try (AnnotationConfigApplicationContext context = SpringCoreApplication.createContext(Map.of())) {
            probe = context.getBean(LifecycleProbe.class);
            assertTrue(probe.isInitialized());
            assertFalse(probe.isDestroyed());
        }

        assertTrue(probe.isDestroyed());
    }

    @Test
    void before_around_and_after_advice_wraps_the_service_call() {
        try (AnnotationConfigApplicationContext context = SpringCoreApplication.createContext(Map.of())) {
            GreetingService service = context.getBean(GreetingService.class);
            MethodLoggingAspect aspect = context.getBean(MethodLoggingAspect.class);
            aspect.clearEvents();

            service.createGreeting("Jarir");

            assertEquals(
                    java.util.List.of(
                            "around:enter",
                            "before:createGreeting",
                            "after:createGreeting",
                            "around:exit"),
                    aspect.events());
        }
    }
}
