package com.jarirahmed.springmvc.config;

import com.jarirahmed.springmvc.controller.PostController;
import com.jarirahmed.springmvc.error.GlobalExceptionHandler;
import com.jarirahmed.springmvc.repository.InMemoryPostRepository;
import com.jarirahmed.springmvc.service.PostService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Plain Spring MVC configuration. Spring Boot will later provide a shorter
 * startup path, but this class makes MVC infrastructure visible first.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {
        PostController.class,
        PostService.class,
        InMemoryPostRepository.class,
        GlobalExceptionHandler.class
})
public class SpringMvcConfiguration {
    @Bean
    public LocalValidatorFactoryBean mvcValidator() {
        return new LocalValidatorFactoryBean();
    }
}
