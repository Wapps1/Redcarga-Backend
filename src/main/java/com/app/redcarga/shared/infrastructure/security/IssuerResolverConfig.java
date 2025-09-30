package com.app.redcarga.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

import java.util.Map;

@Configuration
public class IssuerResolverConfig {

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            AuthenticationProvider firebaseJwtAuthProvider,
            AuthenticationProvider iamJwtAuthProvider,
            @Value("${app.firebase.project-id}") String firebaseProjectId,
            @Value("${iam.jwt.issuer}") String iamIssuer
    ) {
        String firebaseIssuer = "https://securetoken.google.com/" + firebaseProjectId;

        Map<String, AuthenticationManager> managers = Map.of(
                firebaseIssuer, new ProviderManager(firebaseJwtAuthProvider),
                iamIssuer,      new ProviderManager(iamJwtAuthProvider)
        );

        return new JwtIssuerAuthenticationManagerResolver(managers::get);
    }
}
