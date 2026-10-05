package com.jarirahmed.springcore.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A deliberately visible AOP example. The advice records events so tests and
 * the CLI can show when cross-cutting behavior surrounds a service call.
 */
@Aspect
@Component
public class MethodLoggingAspect {
    private static final String GREETING_METHOD =
            "execution(* com.jarirahmed.springcore.service.GreetingService.createGreeting(..))";

    private final List<String> events = new ArrayList<>();

    @Before(GREETING_METHOD)
    public void beforeGreeting(JoinPoint joinPoint) {
        events.add("before:" + joinPoint.getSignature().getName());
    }

    @Around(GREETING_METHOD)
    public Object aroundGreeting(ProceedingJoinPoint joinPoint) throws Throwable {
        events.add("around:enter");
        Object result = joinPoint.proceed();
        events.add("around:exit");
        return result;
    }

    @After(GREETING_METHOD)
    public void afterGreeting(JoinPoint joinPoint) {
        events.add("after:" + joinPoint.getSignature().getName());
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    public void clearEvents() {
        events.clear();
    }
}
