package com.app.redcarga.iam.infrastructure.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IamJwtProperties.class)
public class IamJwtConfig { }
