package com.app.redcarga.iam.interfaces.rest.requests;

public record LogoutRequest(
        Integer sessionId   // requerido por ahora
) { }
