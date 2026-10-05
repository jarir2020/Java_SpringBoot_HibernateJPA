package com.jarirahmed.springboot.controller;

import com.jarirahmed.springboot.config.CourseProperties;
import com.jarirahmed.springboot.model.BootInfoResponse;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/** Shows the configuration and profile state supplied by Spring Boot. */
@RestController
@RequestMapping("/api/boot")
public class BootInfoController {
    private final CourseProperties courseProperties;
    private final Environment environment;

    public BootInfoController(CourseProperties courseProperties, Environment environment) {
        this.courseProperties = courseProperties;
        this.environment = environment;
    }

    @GetMapping("/info")
    public BootInfoResponse info() {
        return new BootInfoResponse(
                environment.getProperty("spring.application.name", "java-spring-learning"),
                courseProperties.name(),
                courseProperties.mode(),
                Arrays.asList(environment.getActiveProfiles()));
    }
}
