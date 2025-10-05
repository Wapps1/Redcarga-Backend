package com.app.redcarga.shared.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenClaims {
    private final org.springframework.core.env.Environment env;

    public TokenClaims(org.springframework.core.env.Environment env) {
        this.env = env;
    }

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
        // 1) Si existe account_id (IAM con claim extra) úsalo
        Object v = jwt.getClaim("account_id");
        if (v instanceof Integer i) return Optional.of(i);
        if (v instanceof String s)  try { return Optional.of(Integer.parseInt(s)); } catch (Exception ignore) {}

        // 2) Si el issuer es IAM, toma el sub como accountId
        String iss = jwt.getClaimAsString("iss");
        String iamIssuer = env.getProperty("iam.jwt.issuer");
        if (iamIssuer != null && iamIssuer.equals(iss)) {
            try { return Optional.of(Integer.parseInt(jwt.getSubject())); }
            catch (Exception ignore) {}
        }
        // 3) Caso contrario (Firebase, etc.) → vacío
        return Optional.empty();
    }

    public boolean hasSystemRole(String role) {
        var jwt = requireJwt();
        var roles = jwt.getClaimAsStringList("sys_roles");
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> role.equalsIgnoreCase(r));
    }
}
