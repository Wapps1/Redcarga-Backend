package com.app.redcarga.media.infrastructure.gateways.cloudinary;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "media.cloudinary")
public class CloudinaryProperties {
    @NotBlank private String cloudName;
    @NotBlank private String apiKey;
    @NotBlank private String apiSecret;

    private Presets presets = new Presets();

    public static class Presets {
        private String requestItemPhoto = "rc_item_signed";
        private String providerLogo = "rc_logo_signed";
        public String getRequestItemPhoto() { return requestItemPhoto; }
        public void setRequestItemPhoto(String v) { this.requestItemPhoto = v; }
        public String getProviderLogo() { return providerLogo; }
        public void setProviderLogo(String v) { this.providerLogo = v; }
    }

    public String getCloudName() { return cloudName; }
    public void setCloudName(String v) { this.cloudName = v; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String v) { this.apiKey = v; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String v) { this.apiSecret = v; }
    public Presets getPresets() { return presets; }
    public void setPresets(Presets p) { this.presets = p; }
}
