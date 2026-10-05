package com.jarirahmed.projects.hrm;

import com.jarirahmed.projects.hrm.config.HrmJpaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** Starts the Project 5 browser application without changing earlier projects. */
@SpringBootApplication
@Import(HrmJpaConfiguration.class)
public class HrmApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HrmApplication.class);
        application.setAdditionalProfiles("project5");
        application.run(args);
    }
}
