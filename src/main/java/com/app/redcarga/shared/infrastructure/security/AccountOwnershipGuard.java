package com.app.redcarga.shared.infrastructure.security;

import com.app.redcarga.identity.application.internal.outboundservices.acl.IamAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountOwnershipGuard {

    private final TokenClaims claims;
    private final IamAccountService iam; // tu ACL al BC IAM

    public int assertOwnershipOrThrow(int requestedAccountId) {
        // Opción 1 (si tienes custom claim): comparar directo y retornar
        var fromClaim = claims.accountIdClaim();
        if (fromClaim.isPresent()) {
            if (!fromClaim.get().equals(requestedAccountId))
                throw new AccessDeniedException("account_mismatch");
            return fromClaim.get();
        }
        // Opción 2 (sin custom claim): resolver por uid/email a través de IAM
        var owner = claims.uid()
                .flatMap(iam::getByExternalUid)   // <-- necesitas exponer este método en tu ACL
                .or(() -> claims.email().flatMap(iam::getByEmail))
                .orElseThrow(() -> new AccessDeniedException("account_not_found"));
        if (owner.accountId() != requestedAccountId) throw new AccessDeniedException("account_mismatch");
        return owner.accountId();
    }
}
