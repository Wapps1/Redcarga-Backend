package com.app.redcarga.iam.interfaces.acl;

public record IamAccountSnapshot(
        int accountId,
        String signupIntentState,   // p.ej. PENDING_EMAIL_VERIFICATION, EMAIL_VERIFIED, BASIC_PROFILE_COMPLETED
        String systemRoleCode,      // p.ej. CLIENT, PROVIDER
        Boolean emailVerified       // opcional, puede ser null si no quieres exponerlo
) { }