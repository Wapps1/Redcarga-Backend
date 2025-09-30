package com.app.redcarga.iam.interfaces.rest.requests;

public record RegisterStartRequest(
        String email,
        String username,
        String password,       // viene como String del JSON
        String roleCode,       // CLIENT|PROVIDER|PLATFORM_ADMIN
        String platform,       // WEB|ANDROID|IOS
        String idempotencyKey  // opcional
) { }
