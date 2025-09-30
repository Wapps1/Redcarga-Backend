package com.app.redcarga.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {
    @Bean
    public Clock clock() {
        // Usa UTC para evitar sorpresas con zonas horarias
        return Clock.systemUTC();
    }
}
