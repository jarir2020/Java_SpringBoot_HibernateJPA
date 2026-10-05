package com.jarirahmed.springcore.config;

import com.jarirahmed.springcore.aop.MethodLoggingAspect;
import com.jarirahmed.springcore.lifecycle.LifecycleProbe;
import com.jarirahmed.springcore.service.GreetingService;
import com.jarirahmed.springcore.service.PrototypeMarker;
import com.jarirahmed.springcore.service.SystemTimeSource;
import com.jarirahmed.springcore.service.TimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * The composition root for the plain Spring Core lesson.
 *
 * <p>The configuration class tells Spring what to scan and which objects to
 * create explicitly. Spring Boot will automate much of this later, but seeing
 * the root directly is important for understanding the container.</p>
 */
@Configuration
@ComponentScan(basePackageClasses = {
        GreetingService.class,
        MethodLoggingAspect.class,
        LifecycleProbe.class
})
@EnableAspectJAutoProxy
public class SpringCoreConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public TimeSource timeSource() {
        return new SystemTimeSource();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrototypeMarker prototypeMarker() {
        return new PrototypeMarker();
    }
}
