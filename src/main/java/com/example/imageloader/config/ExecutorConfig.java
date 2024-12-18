package com.example.imageloader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10); // İhtiyacımıza göre thread sayıs
    }
}