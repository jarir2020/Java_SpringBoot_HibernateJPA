package com.jarirahmed.springapi.config;

import com.jarirahmed.springapi.controller.EmployeeController;
import com.jarirahmed.springapi.error.EmployeeApiExceptionHandler;
import com.jarirahmed.springapi.repository.EmployeeRepository;
import com.jarirahmed.springapi.service.EmployeeService;
import com.jarirahmed.springtransactions.SpringTransactionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Phase 9 composition root. It reuses Phase 8's JPA and transaction setup but
 * adds only the API layer components needed for this lesson.
 */
@Configuration
@EnableWebMvc
@Import(SpringTransactionConfiguration.class)
@ComponentScan(basePackageClasses = {
        EmployeeController.class,
        EmployeeService.class,
        EmployeeRepository.class,
        EmployeeApiExceptionHandler.class
})
public class SpringApiConfiguration {
    @Bean
    public LocalValidatorFactoryBean mvcValidator() {
        return new LocalValidatorFactoryBean();
    }
}
