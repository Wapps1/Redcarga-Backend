package com.app.redcarga.iam.interfaces.rest.responses;

import java.util.List;

public record LoginIncompleteResponse(
        String reason,
        String signupStatus,
        Integer accountId,
        List<String> pending,
        String nextStep
) {
    public static LoginIncompleteResponse of(String status, Integer accountId, List<String> pending, String next) {
        return new LoginIncompleteResponse("REGISTRATION_INCOMPLETE", status, accountId, pending, next);
    }
}
