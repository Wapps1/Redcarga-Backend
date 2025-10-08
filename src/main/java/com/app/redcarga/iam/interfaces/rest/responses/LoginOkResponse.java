package com.app.redcarga.iam.interfaces.rest.responses;

import java.util.List;

public record LoginOkResponse(
        Integer sessionId,
        Integer accountId,
        String accessToken,
        long expiresIn,
        long expiresAt,
        String tokenType,
        String status,
        List<String> roles,
        AccountInfo account
) {
    public static LoginOkResponse of(Integer sessionId, String token, long exp) {
        return new LoginOkResponse(sessionId, null, token, exp, 0L, "Bearer", "ACTIVE", List.of(), null);
    }
    
    public static LoginOkResponse of(Integer sessionId, Integer accountId, String token, long exp, 
                                   long expiresAt, List<String> roles, AccountInfo account) {
        return new LoginOkResponse(sessionId, accountId, token, exp, expiresAt, "Bearer", "ACTIVE", roles, account);
    }
    
    public record AccountInfo(
            String username,
            String email,
            boolean emailVerified,
            long updatedAt
    ) {}
}
