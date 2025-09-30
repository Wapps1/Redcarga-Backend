package com.app.redcarga.iam.application.internal.views;

public record RegisterStartResult(
        Integer accountId,
        Integer signupIntentId,
        String email,
        boolean emailVerified,
        String verificationLink // dev: opcional devolverlo; en prod, envías correo y puedes devolver null
) { }
