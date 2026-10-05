package com.jarirahmed.projects.storefront;

import com.jarirahmed.projects.storefront.config.StorefrontJpaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** Spring Boot entry point for the Project 4 JPA storefront. */
@SpringBootApplication
@Import(StorefrontJpaConfiguration.class)
public class StorefrontApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(StorefrontApplication.class);
        application.setAdditionalProfiles("project4");
        application.run(args);
    }
}
