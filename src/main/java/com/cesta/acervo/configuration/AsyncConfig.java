package com.cesta.acervo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor", destroyMethod = "close")
    public Executor taskExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                .name("acervo-vt-", 0)
                .factory();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);
        return executor;
    }
}
