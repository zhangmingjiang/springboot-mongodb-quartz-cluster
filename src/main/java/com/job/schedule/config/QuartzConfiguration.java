package com.job.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfiguration {

    public static final String CONTEXT_KEY = "applicationContext";

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        scheduler.setConfigLocation(resourceLoader.getResource("classpath:quartz.properties"));
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        return scheduler;
    }
}