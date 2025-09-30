package com.app.redcarga.identity.application.internal.outboundservices.acl;

import java.util.Optional;

public interface IamAccountService {
    Optional<AccountSnapshot> getByAccountId(int accountId);
    Optional<AccountSnapshot> getByExternalUid(String uid);  // NUEVO
    Optional<AccountSnapshot> getByEmail(String email);      // NUEVO

    record AccountSnapshot(
            int accountId,
            String signupIntentState,
            String systemRoleCode,
            Boolean emailVerified
    ) {}
}