package com.app.redcarga.iam.interfaces.rest.requests;

public record LoginRequest(
        String platform,   // WEB|ANDROID|IOS
        String ip,         // opcional
        Long ttlSeconds    // opcional (default en controller si viene null)
) { }