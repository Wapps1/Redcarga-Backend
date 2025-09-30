package com.app.redcarga.iam.infrastructure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "iam.jwt")
public record IamJwtProperties(
        String issuer,
        String audience,
        String kid,
        Long ttlSeconds,               // ej. 7200 (2h en dev)
        String privateKeyLocation,     // file:./.secrets/iam/dev/iam_private.pem
        String publicKeyLocation       // para JWKS más adelante
) {
    public Duration ttl() { return Duration.ofSeconds(ttlSeconds != null ? ttlSeconds : 900L); }
}
