package com.jarirahmed.springcore.demo;

import com.jarirahmed.springcore.aop.MethodLoggingAspect;
import com.jarirahmed.springcore.container.SpringCoreApplication;
import com.jarirahmed.springcore.lifecycle.LifecycleProbe;
import com.jarirahmed.springcore.service.GreetingService;
import com.jarirahmed.springcore.service.PrototypeMarker;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/** Runs the Phase 2 examples from one small command-line lesson. */
public final class SpringCoreLesson {
    private SpringCoreLesson() {
    }

    public static void run() {
        System.out.println("\n=== PHASE 2: SPRING CORE ===");

        LifecycleProbe lifecycleProbe;
        try (AnnotationConfigApplicationContext context = SpringCoreApplication.createContext(
                Map.of("app.greeting-prefix", "Welcome"))) {
            GreetingService firstService = context.getBean(GreetingService.class);
            GreetingService secondService = context.getBean(GreetingService.class);
            PrototypeMarker firstMarker = context.getBean(PrototypeMarker.class);
            PrototypeMarker secondMarker = context.getBean(PrototypeMarker.class);
            lifecycleProbe = context.getBean(LifecycleProbe.class);

            System.out.println("Greeting: " + firstService.createGreeting("  Jarir  "));
            System.out.println("Singleton service shared: " + (firstService == secondService));
            System.out.println("Prototype marker is new: " +
                    !firstMarker.instanceId().equals(secondMarker.instanceId()));
            System.out.println("Lifecycle initialized: " + lifecycleProbe.isInitialized());
            System.out.println("AOP events: " + context.getBean(MethodLoggingAspect.class).events());
        }

        System.out.println("Lifecycle destroyed after context close: " + lifecycleProbe.isDestroyed());
        System.out.println("PHASE 2 COMPLETE");
    }
}
