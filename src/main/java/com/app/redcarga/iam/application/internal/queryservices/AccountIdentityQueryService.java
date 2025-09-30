package com.app.redcarga.iam.application.internal.queryservices;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public interface AccountIdentityQueryService {
    Optional<Integer> resolveAccountId(JwtAuthenticationToken principal);
}
