package com.app.redcarga.iam.infrastructure.firebase;

import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.google.firebase.auth.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FirebaseAuthGatewayImpl implements AuthProviderGateway {

    private final FirebaseAuth auth;

    public FirebaseAuthGatewayImpl(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public String createUser(String email, char[] rawPassword) {
        try {
            UserRecord.CreateRequest req = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(new String(rawPassword))
                    .setEmailVerified(false)
                    .setDisabled(false);
            UserRecord record = auth.createUser(req);
            return record.getUid();
        } catch (Exception e) {
            throw new IllegalStateException("Firebase createUser failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void setCustomClaims(String externalUid, Map<String, Object> claims) {
        try {
            auth.setCustomUserClaims(externalUid, claims);
        } catch (Exception e) {
            throw new IllegalStateException("Firebase setCustomClaims failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateEmailVerificationLink(String email, String continueUrl) {
        try {
            ActionCodeSettings settings = ActionCodeSettings.builder()
                    .setUrl(continueUrl)
                    .setHandleCodeInApp(true)
                    .build();

            return auth.generateEmailVerificationLink(email, settings);
        } catch (Exception e) {
            throw new IllegalStateException("Firebase generateEmailVerificationLink failed: " + e.getMessage(), e);
        }
    }


    @Override
    public void revokeRefreshTokens(String externalUid) {
        try {
            auth.revokeRefreshTokens(externalUid);
        } catch (Exception e) {
            throw new IllegalStateException("Firebase revokeRefreshTokens failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isEmailVerified(String email) {
        try {
            UserRecord record = auth.getUserByEmail(email);
            return record.isEmailVerified();
        } catch (Exception e) {
            throw new IllegalStateException("Firebase isEmailVerified failed: " + e.getMessage(), e);
        }
    }
}
