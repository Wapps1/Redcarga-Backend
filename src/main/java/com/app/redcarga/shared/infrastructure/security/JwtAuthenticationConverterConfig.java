package com.app.redcarga.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class JwtAuthenticationConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        Converter<Jwt, Collection<GrantedAuthority>> rolesConverter = this::mapAuthorities;
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }

    private Collection<GrantedAuthority> mapAuthorities(Jwt jwt) {
        Object claim = jwt.getClaim("sys_roles");
        if (!(claim instanceof List<?> roles)) return List.of();

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::toUpperCase)
                .map(code -> "ROLE_" + code)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }
}
