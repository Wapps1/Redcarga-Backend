package com.app.redcarga.media.infrastructure.config;

import com.app.redcarga.media.infrastructure.gateways.cloudinary.CloudinaryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CloudinaryProperties.class)
public class MediaConfig { }
