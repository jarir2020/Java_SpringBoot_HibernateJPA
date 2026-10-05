package com.jarirahmed.springboot;

import com.jarirahmed.springmvc.controller.PostController;
import com.jarirahmed.springmvc.error.GlobalExceptionHandler;
import com.jarirahmed.springmvc.repository.InMemoryPostRepository;
import com.jarirahmed.springmvc.service.PostService;
import com.jarirahmed.springboot.config.CourseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Phase 4 entry point: Boot owns the application startup and MVC setup.
 *
 * The Phase 3 components are imported explicitly so the contrast is visible:
 * Boot's application class does not need the hand-built MVC configuration.
 */
/**
 * Phase 4 remains an intentionally unsecured MVC lesson. Phase 10 supplies
 * its own explicit SecurityFilterChain instead of changing this earlier app.
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class
})
@EnableConfigurationProperties(CourseProperties.class)
@Import({
        PostController.class,
        PostService.class,
        InMemoryPostRepository.class,
        GlobalExceptionHandler.class
})
public class SpringBootLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootLearningApplication.class, args);
    }

    public static ConfigurableApplicationContext run(String... args) {
        return SpringApplication.run(SpringBootLearningApplication.class, args);
    }
}
