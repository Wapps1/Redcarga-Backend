package com.app.redcarga.iam.interfaces.rest.responses;

public record LoginOkResponse(
        Integer sessionId,
        String accessToken,
        long expiresIn,
        String tokenType,
        String status
) {
    public static LoginOkResponse of(Integer sessionId, String token, long exp) {
        return new LoginOkResponse(sessionId, token, exp, "Bearer", "ACTIVE");
    }
}
