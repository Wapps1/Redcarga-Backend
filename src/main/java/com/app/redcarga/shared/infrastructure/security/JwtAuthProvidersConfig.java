package com.app.redcarga.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

@Configuration
public class JwtAuthProvidersConfig {

    @Bean
    public AuthenticationProvider iamJwtAuthProvider(
            JwtDecoder iamJwtDecoder,
            JwtAuthenticationConverter converter) {
        var p = new JwtAuthenticationProvider(iamJwtDecoder);
        p.setJwtAuthenticationConverter(converter);
        return p;
    }

    @Bean
    public AuthenticationProvider firebaseJwtAuthProvider(
            JwtDecoder firebaseJwtDecoder,
            JwtAuthenticationConverter converter) {
        var p = new JwtAuthenticationProvider(firebaseJwtDecoder);
        p.setJwtAuthenticationConverter(converter);
        return p;
    }
}
