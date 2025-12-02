package com.app.redcarga.media.infrastructure.cloudinary;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "media.cloudinary")
@Validated
@Getter
@Setter
public class CloudinaryConfig {
    
    @NotBlank
    private String cloudName;
    
    @NotBlank
    private String apiKey;
    
    @NotBlank
    private String apiSecret;
}
