// requests/application/internal/config/RequestsSchedulingConfig.java
package com.app.redcarga.requests.application.internal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(RequestsOutboxProperties.class)
@RequiredArgsConstructor
public class RequestsSchedulingConfig {}
