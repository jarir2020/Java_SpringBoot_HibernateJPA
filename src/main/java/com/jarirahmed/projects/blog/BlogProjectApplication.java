package com.jarirahmed.projects.blog;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;

/**
 * Spring Boot entry point for the full-stack Project 3 application.
 *
 * <p>The package is intentionally separate from the earlier phase examples.
 * Boot scans this project only, so its browser API and in-memory data do not
 * change the existing lesson applications.</p>
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class
})
public class BlogProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogProjectApplication.class, args);
    }
}
