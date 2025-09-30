package com.app.redcarga.iam.application.internal.queryservices;

import com.app.redcarga.iam.domain.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AccountIdentityQueryServiceImpl implements AccountIdentityQueryService {

    private final AccountRepository accountRepository;
    private final String firebaseIssuer;
    private final String iamIssuer;

    public AccountIdentityQueryServiceImpl(AccountRepository accountRepository,
                                      @Value("${app.firebase.project-id}") String firebaseProjectId,
                                      @Value("${iam.jwt.issuer}") String iamIssuer) {
        this.accountRepository = accountRepository;
        this.firebaseIssuer = "https://securetoken.google.com/" + firebaseProjectId;
        this.iamIssuer = iamIssuer;
    }

    @Override
    public java.util.Optional<Integer> resolveAccountId(JwtAuthenticationToken principal) {
        var claims = principal.getToken().getClaims();
        String iss = (String) claims.get("iss");

        if (iamIssuer.equals(iss)) {
            // IAM: sub = accountId
            try {
                return java.util.Optional.of(Integer.parseInt((String) claims.get("sub")));
            } catch (Exception e) {
                return java.util.Optional.empty();
            }
        }

        if (firebaseIssuer.equals(iss)) {
            // Firebase: uid en sub/user_id -> buscar Account por externalUid
            String uid = (String) (claims.getOrDefault("sub", claims.get("user_id")));
            if (uid == null || uid.isBlank()) return java.util.Optional.empty();

            return accountRepository.findByExternalUid(uid).map(a -> a.getId());
        }

        return java.util.Optional.empty();
    }
}
