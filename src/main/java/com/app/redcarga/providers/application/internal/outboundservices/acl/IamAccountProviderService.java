package com.app.redcarga.providers.application.internal.outboundservices.acl;
import java.util.Optional;

public interface IamAccountProviderService {
    Optional<AccountProviderSnapshot> getByAccountId(int accountId);
    Optional<AccountProviderSnapshot> getByExternalUid(String uid);
    Optional<AccountProviderSnapshot> getByEmail(String email);
    record AccountProviderSnapshot(int accountId, String signupIntentState, String systemRoleCode, Boolean emailVerified) {}
}
