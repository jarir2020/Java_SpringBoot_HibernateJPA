package com.jarirahmed.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Type-safe binding for the external course.* configuration namespace. */
@ConfigurationProperties(prefix = "course")
public record CourseProperties(String name, String mode) {
}
