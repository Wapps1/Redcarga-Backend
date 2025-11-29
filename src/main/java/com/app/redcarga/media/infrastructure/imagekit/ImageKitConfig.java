package com.app.redcarga.media.infrastructure.imagekit;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "media.imagekit")
@Validated
@Getter
@Setter
public class ImageKitConfig {

    @NotBlank
    private String urlEndpoint;

    @NotBlank
    private String publicKey;

    @NotBlank
    private String privateKey;
}
