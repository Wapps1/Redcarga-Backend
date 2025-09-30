package com.app.redcarga.shared.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenClaims {
    public Jwt requireJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) throw new IllegalStateException("Missing JWT");
        return (Jwt) auth.getPrincipal();
    }
    public Optional<String> uid() {
        var jwt = requireJwt();
        var uid = Optional.ofNullable(jwt.getClaimAsString("sub")).orElse(jwt.getClaimAsString("user_id"));
        return Optional.ofNullable(uid).filter(s -> !s.isBlank());
    }
    public Optional<String> email() {
        var jwt = requireJwt();
        var email = jwt.getClaimAsString("email");
        return Optional.ofNullable(email).filter(s -> !s.isBlank());
    }
    public Optional<Integer> accountIdClaim() {
        var jwt = requireJwt();
        return Optional.ofNullable(jwt.getClaim("account_id"));
    }
}
