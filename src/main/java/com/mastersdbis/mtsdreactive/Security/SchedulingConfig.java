package com.mastersdbis.mtsdreactive.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // @Scheduled methods in TaskService fire on the Spring scheduler thread pool.
    // They must call .subscribe() to trigger the reactive pipeline —
    // they cannot block the scheduler thread.
}