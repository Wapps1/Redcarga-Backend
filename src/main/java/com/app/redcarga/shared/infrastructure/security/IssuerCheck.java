package com.app.redcarga.shared.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class IssuerCheck {
    private final String iamIssuer;
    private final String firebaseIssuer;

    public IssuerCheck(@Value("${iam.jwt.issuer}") String iamIssuer,
                       @Value("${app.firebase.project-id}") String firebaseProjectId) {
        this.iamIssuer = iamIssuer;
        this.firebaseIssuer = "https://securetoken.google.com/" + firebaseProjectId;
    }

    public boolean isIam(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwt) {
            return iamIssuer.equals(jwt.getToken().getClaims().get("iss"));
        }
        return false;
    }

    public boolean isFirebase(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwt) {
            return firebaseIssuer.equals(jwt.getToken().getClaims().get("iss"));
        }
        return false;
    }
}
