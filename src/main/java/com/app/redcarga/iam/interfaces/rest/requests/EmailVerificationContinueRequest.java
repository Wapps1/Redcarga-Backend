package com.app.redcarga.iam.interfaces.rest.requests;

import org.springframework.web.bind.annotation.RequestParam;

public record EmailVerificationContinueRequest(
        String email,
        Integer accountId,
        String redirect
) { }