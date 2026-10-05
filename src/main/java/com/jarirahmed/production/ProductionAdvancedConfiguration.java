package com.jarirahmed.production;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Explicit Phase 12 composition root. Production applications can replace the
 * local cache manager and executor with Redis and infrastructure-sized pools.
 */
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
@ComponentScan(basePackageClasses = ProductionAdvancedConfiguration.class)
public class ProductionAdvancedConfiguration {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("advancedEmployees");
    }

    @Bean(name = "advancedTaskExecutor")
    public ThreadPoolTaskExecutor advancedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("advanced-worker-");
        return executor;
    }

    @Bean
    public ThreadPoolTaskScheduler advancedTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("advanced-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(5);
        return scheduler;
    }
}
