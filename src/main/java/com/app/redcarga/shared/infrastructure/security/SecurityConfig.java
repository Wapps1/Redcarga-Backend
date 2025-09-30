package com.app.redcarga.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
            AuthorizationManager<RequestAuthorizationContext> iamOnly,
            AuthorizationManager<RequestAuthorizationContext> firebaseOrIam
    ) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/iam/register-start",
                                "/iam/email-verification/continue",
                                "/_dev/**"
                        ).permitAll()

                        .requestMatchers("/identity/verify-and-create").access(firebaseOrIam)
                        // onboarding: acepta Firebase (y también IAM si llega)
                        .requestMatchers("/iam/login", "/iam/resend-email-verification").access(firebaseOrIam)

                        // resto: SOLO IAM
                        .anyRequest().access(iamOnly)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(authenticationManagerResolver)
                );

        return http.build();
    }
}
